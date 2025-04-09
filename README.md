# 🔄 Offline Data Sharing Between Android Apps using Ktor and QR Code

This open-source Android project allows **offline, secure image transfer** between two Android apps (App A and App B) over a local network using **Ktor**, **QR codes**, and **token-based authentication**.

---

## 📱 Use Case

- **App A**: Acts as a **local Ktor server**, generates a **QR code** with IP, port, and token, and receives an image from the client.
- **App B**: Acts as a **client**, scans the QR, picks an image, converts it to **Base64**, and sends it to App A securely.

---

## 📦 Tech Stack

- **Kotlin**
- **Ktor (CIO Engine)**
- **Jetpack Lifecycle**
- **ZXing QR Scanner**
- **Base64 Image Encoding**
- **Modular MVVM Architecture**

---

## 🧱 Architecture Overview

![Architecture Diagram](docs/architecture.png)

---

## 📂 Project Structure

