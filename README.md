# KiraCash

KiraCash is a comprehensive Android application designed to manage personal finances. It provides users with the tools to track expenses, manage debts, scan receipts, and visualize financial data with ease.

## Installation

Download:

    $ git clone https://github.com/yourusername/KiraCash.git

Import the project into Android Studio using the menu option: `File > New > Import Project...`

Build and run the project on your device or emulator by selecting `Run > Run 'app'`.

## Usage Examples

### Main Screen

The main screen provides an overview of your financial status, including expenses, debts, and quick actions.

### Adding a Wallet

1. Navigate to the profile menu.
2. Select "Edit Wallet".
3. Add a new wallet by filling out the necessary details and uploading an image if desired.

### Scanning a Receipt

1. Navigate to the QR menu.
2. Use the camera to scan a receipt.
3. Confirm the extracted items and assign them to the appropriate wallets.

### Viewing Statistics

1. Navigate to the statistics screen.
2. Toggle between viewing amounts you owe and amounts owed to you.
3. View detailed charts and lists of financial data.

## Configuration Options

### Gemini API Configuration

To configure the Gemini API for receipt processing:

1. Navigate to the profile menu.
2. Select "Configure Gemini API".
3. Enter your API key and save the configuration.

## Contribution Guidelines

To contribute:

1. Fork the repository.
2. Create a new branch:

        $ git checkout -b feature-branch

3. Make your changes and commit them:

        $ git commit -am 'Add new feature'

4. Push to the branch:

        $ git push origin feature-branch

5. Create a new Pull Request.

## Testing Instructions

To test KiraCash:

1. Ensure that the project is properly set up in Android Studio.
2. Run the project on a physical device or emulator.
3. Use the various features and report any bugs or issues via GitHub issues.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## Acknowledgements/Credits

- [Google](https://developers.google.com/ml-kit/vision/text-recognition) for the ML Kit.
- [Room Persistence Library](https://developer.android.com/jetpack/androidx/releases/room) for the database management.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for the UI components.

Feel free to reach out with any questions or feedback. Happy tracking!