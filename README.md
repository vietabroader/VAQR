# VAQR
Before an event starts, we use the [VAQR web application](https://vietabroader-qr.appspot.com) to generate QR codes and send them out to attendees. The lists of attendees are kept in a Google sheet.
 
On the day of the event, we use this application to connect to that sheet and check attendees in as they give us their QR code.
## Table of content
- [Installation](#installation)
- [Instruction](#instruction)
    - [Generate QR Code](#generate-qr-code)
    - [Scan QR Code](#scan-qr-code)
   
## Installation
1. Install [Java Runtime Environment](https://java.com/en/download/).
2. Go to [Releases page](https://github.com/vietabroader/VAQR/releases) to get the latest binary for either macOS or Windows.

## Instruction
### Generate QR Code
- Open the app then click on **Go to QR Generator** to go to the web application, or enter [vietabroader-qr.appspot.com](https://vietabroader-qr.appspot.com) directly into your web browser.
- Click **Connect to Google Drive** and use the Google account that will be used to hold the generated QR code images to sign in.
- Copy folder ID into the **Google Drive folder ID** text field as instructed on the page. Be sure to share this folder to the attendees so that they can access their codes.
- In the attendees sheet, there should be a column holding their emails. Copy this column to the **Email List** text field then click **Generate**.

After a while, the application will generate and upload the corresponding QR code images, then return the list of Google drive links

<img src = "https://user-images.githubusercontent.com/18899970/27971357-09bb7dc2-6318-11e7-8999-4f91a6e057a9.png" width = "500"/>

### Scan QR Code
- Sign in your Google Account
- Copy your Google Sheet ID into Spreadsheet ID

*Where to find Sheet ID*
    <img src = "https://user-images.githubusercontent.com/18899970/27970654-43c24cba-6315-11e7-91ed-945db7bc16a7.png"/>
- Insert information into Workspace section. Click Refresh after you finish or make any changes.
    - The Key column is usually the Email column and the Output is the column for marking attendance
    
*Sample*
    <img src = "https://user-images.githubusercontent.com/18899970/27970797-b5793c56-6315-11e7-92f1-37653e638021.png"/>
- Click on Start Webcam to open webcam and scan QR code from attendees
    - After a QR code being scanned, a mark will appear in the Output column next to the email
        <img src = "https://user-images.githubusercontent.com/18899970/27972421-ed952c0c-631b-11e7-9044-c1ce6513c394.png">

