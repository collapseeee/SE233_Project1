package se233.se233_project1.controller;

import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffprobe.*;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import se233.se233_project1.model.FileEntry;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

public class Converter implements Callable<FileEntry> {
    Logger logger = LogManager.getLogger(Converter.class);

    private final FileEntry entry;
    private final String outputFormat;
    private final String constantBitrate;
    private final String variableBitrate;
    private final String sampleRate;
    private final String channel;
    private final boolean fadeIn;
    private final boolean fadeOut;
    private final boolean reverse;
    private final String tempOutputPath;

    private final ProgressCallback progressCallback;

    public Converter(FileEntry entry, String outputFormat, String constantBitrate, String variableBitrate, String sampleRate,
                     String channel, boolean fadeIn, boolean fadeOut, boolean reverse, ProgressCallback progressCallback) {
        this.entry = entry;
        this.outputFormat = outputFormat;
        this.constantBitrate = constantBitrate;
        this.variableBitrate = variableBitrate;
        this.sampleRate = sampleRate;
        this.channel = channel;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.reverse = reverse;
        this.tempOutputPath = entry.getFilePath()
                .replaceFirst("\\.[^.]+$", "") + "_converted." + outputFormat;
        this.progressCallback = progressCallback;
    }

    @Override
    public FileEntry call() throws Exception {
        System.out.println("Converting " + entry.getFileName() + " to " + outputFormat);

        Path ffmpegDir = extractFfmpegFolder();
        Path inputPath = Paths.get(entry.getFilePath());
        Path outputPath = Paths.get(tempOutputPath);

        FFprobe ffprobe = FFprobe.atPath(ffmpegDir);
        FFprobeResult probeResult = ffprobe.setShowStreams(true).setInput(inputPath).execute();

        double totalDuration;
        if (probeResult.getFormat() != null && probeResult.getFormat().getDuration() != null) {
            totalDuration = probeResult.getFormat().getDuration();
        } else {
            totalDuration = 1.0;
        }
        System.out.println("Total duration: " + totalDuration);

        FFmpeg ffmpeg = FFmpeg.atPath(ffmpegDir).addInput(UrlInput.fromPath(inputPath))
                .addOutput(UrlOutput.toPath(outputPath)).setOverwriteOutput(true);

        List<String> args = new ArrayList<>();
        args.add("-ar"); args.add(sampleRate.replace(" Khz", ""));
        args.add("-ac"); args.add(channel);

        switch (outputFormat) {
            case "mp3": {
                if (constantBitrate != null && !constantBitrate.isEmpty()) {
                    args.add("-b:a");
                    args.add(constantBitrate.replace(" kbps", "k"));
                } else if (variableBitrate != null && !variableBitrate.isEmpty()) {
                    args.add("-q:a");
                    args.add(variableBitrate);
                }
                args.add("-f"); args.add("mp3");
                break;
            }
            case "wav": {
                args.add("-acodec"); args.add("pcm_s16le");
                args.add("-f"); args.add("wav");
                break;
            }
            case "m4a": {
                args.add("-c:a"); args.add("aac");
                args.add("-b:a"); args.add(constantBitrate.replace(" kbps", "k"));
                args.add("-f"); args.add("m4a");
                break;
            }
            case "flac": {
                args.add("-c:a"); args.add("flac");
                args.add("-f"); args.add("flac");
                break;
            }
            case "ogg": {
                args.add("-c:a"); args.add("libvorbis");
                args.add("-b:a"); args.add(constantBitrate.replace(" kbps", "k"));
                args.add("-f"); args.add("ogg");
                break;
            }
            case "mp2": {
                args.add("-c:a"); args.add("mp2");
                args.add("-b:a"); args.add(constantBitrate.replace(" kbps", "k"));
                args.add("-f"); args.add("mp2");
                break;
            }
        }

        StringBuilder chain = new StringBuilder();
        if (fadeIn)  chain.append("afade=t=in:ss=0:d=3,");
        if (fadeOut) {
            double d = 3.0;
            double st = Math.max(0.0, totalDuration - d);
            chain.append("afade=t=out:st=").append(st).append(":d=").append(d).append(",");
        }
        if (reverse) chain.append("areverse,");
        if (chain.length() > 0) {
            chain.setLength(chain.length() - 1); // trim trailing comma
            args.add("-filter:a"); args.add(chain.toString());
        }

        String arguments = String.join(" ", args);
        for (String a : args) {
            ffmpeg.addArgument(a);
        }
        System.out.println("FFmpeg arguments: " + arguments);

        AtomicReference<Double> lastProgress = new AtomicReference<>(0.0);
        ffmpeg.setProgressListener(progress -> {
           double currTime = progress.getTimeMillis() / 1000.0;
           double ratio = Math.min(1.0, (currTime / totalDuration));
           if (ratio - lastProgress.get() > 0.01) {
               lastProgress.set(ratio);
               if (progressCallback != null) {
                   Platform.runLater(() -> progressCallback.onProgress(ratio));
               }
           }
        });

        ffmpeg.execute();
        System.out.println("FFmpeg executed successfully.");

        return new FileEntry(entry.getFileName() + "(converted)", outputPath.toString());
    }

    private Path extractFfmpegFolder() throws IOException {
        Path tempDir = Files.createTempDirectory("ffmpeg");

        String[] binaries = {"bin/ffmpeg.exe", "bin/ffprobe.exe"};

        for (String binary : binaries) {
            try (InputStream in = getClass().getResourceAsStream("/se233/se233_project1/ffmpeg/" + binary)) {
                if (in == null) throw new IOException("File not found: " + binary + " in resources.");
                Path outFile = tempDir.resolve(binary.substring(binary.lastIndexOf("/") + 1));
                Files.copy(in, outFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                outFile.toFile().setExecutable(true);
            }
        }
        return tempDir;
    }

    public interface ProgressCallback {
        void onProgress(double progress);
    }
}
