# TeeYai Converter

A JavaFX-based audio file converter application that supports multiple audio formats with advanced conversion options including audio effects and quality presets.

## Features

### Supported Formats
- **Input formats**: WAV, MP3, M4A, FLAC
- **Output formats**: MP3, WAV, M4A, FLAC, MP2

### Conversion Options

#### Audio Quality Settings
- **Constant Bitrate (CBR)**: Fixed bitrate encoding
- **Variable Bitrate (VBR)**: Quality-based encoding (MP3 only)
- **Quality Presets**: Best, Good, Standard, Economy
- **Sample Rate**: Configurable from 8kHz to 96kHz (format-dependent)
- **Channels**: Mono or Stereo

#### Audio Effects
- **Fade In**: 3-second fade in at the start
- **Fade Out**: 3-second fade out at the end
- **Reverse**: Reverse the audio playback

### User Interface Features
- **Drag & Drop Support**: Easily add files by dragging them into the application
- **File Browser**: Traditional file selection dialog
- **Batch Conversion**: Convert multiple files simultaneously
- **Real-time Progress**: Visual progress bars for each conversion
- **Preview & Download**: Quick access to converted files

## Technical Details

### Technologies Used
- **JavaFX**: GUI framework
- **Jaffree**: FFmpeg wrapper for Java
- **FFmpeg**: Audio processing engine
- **Log4j2**: Logging framework

### Architecture
- **MVC Pattern**: Separation of concerns with Model-View-Controller
- **Multi-threading**: Concurrent conversions using ExecutorService (4 threads)
- **Custom Exception Handling**: Dedicated AudioConversionException for error management

### Key Components

#### Models
- `FileEntry`: Represents input audio files
- `OutputItem`: Tracks conversion progress and status

#### Controllers
- `MainViewController`: Main application logic and UI management
- `Converter`: Handles FFmpeg conversion process
- `OutputItemCellController`: Manages individual output list items

#### Exception Handling
- `AudioConvertionException`: Custom exception for conversion errors

## Format-Specific Settings

### MP3
- Bitrates: 32-320 kbps
- VBR Quality: 0-9 (0 = highest quality)
- Sample Rates: 32kHz, 44.1kHz, 48kHz

### WAV
- Codec: PCM 16-bit
- Sample Rates: 8kHz - 96kHz
- Uncompressed format

### M4A (AAC)
- Bitrates: 16-512 kbps
- Sample Rates: 8kHz - 48kHz
- Fast start optimization

### FLAC
- Lossless compression
- Sample Rates: 8kHz - 48kHz

### MP2
- Bitrates: 64-320 kbps
- Sample Rates: 22.05kHz - 48kHz

## Installation & Running

### Prerequisites
- Java 11 or higher
- JavaFX SDK
- FFmpeg binaries (included in resources)

### Building
```bash
# Using Maven
mvn clean install

# Run the application
mvn javafx:run
```

### Running
```bash
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml \
     -jar TeeYaiConverter.jar
```

## Project Structure
```
se233.se233_project1/
├── controller/
│   ├── MainViewController.java
│   ├── Converter.java
│   └── OutputItemCellController.java
├── model/
│   ├── FileEntry.java
│   └── OutputItem.java
├── exception/
│   └── AudioConvertionException.java
├── Launcher.java
└── resources/
    └── ffmpeg/bin/
```

## Usage

1. **Add Files**: Click "Select Files" or drag and drop audio files into the designated area
2. **Choose Format**: Select the desired output format (MP3, WAV, M4A, FLAC, MP2)
3. **Configure Settings**:
   - Select quality preset or custom bitrate
   - Choose sample rate and channel configuration
   - Enable audio effects if desired (Fade In/Out, Reverse)
4. **Convert**: Click the "Convert" button to start batch conversion
5. **Access Files**: Use Preview or Download buttons to access converted files

## Error Handling

The application provides comprehensive error handling:
- I/O exceptions during file operations
- FFmpeg internal errors
- Interrupted conversions
- User-friendly error dialogs with detailed messages

## Graceful Shutdown

The application includes a confirmation dialog on exit and properly shuts down the executor service to prevent resource leaks.

## License

This project is created for educational purposes as part of SE233 coursework.

## Authors

Developed as SE233 Project 1
