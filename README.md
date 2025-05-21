# CarRentalMS-Frontend
A modern Java Swing desktop application for managing car rentals, featuring role-based dashboards, REST API integration, and responsive UI components.

## Features

- **Dual Interface**: Separate dashboards for Admin and Users
- **CRUD Operations**: 
  - Car inventory management
  - User management (Admin only)
  - Booking lifecycle handling
- **Authentication**: Secure login/signup flow
- **Real-time Data**: API synchronization with backend
- **Custom UI Components**: 
  - Themed tables with sorting
  - Animated sidebars
  - Responsive cards

## Tech Stack

- **Frontend**: Java Swing
- **API Client**: Java 11 HttpClient
- **JSON Processing**: Jackson Databind
- **Date Picker**: JCalendar
- **Build Tool**: Maven

## Project Structure
carrental-Frontend/
├── src/
│ └── com/Buildex/
│ ├── api/ # API communication
│ ├── auth/ # Authentication
│ ├── component/ # UI components
│ ├── form/ # Main screens
│ ├── model/ # Data models
│ ├── swing/ # Custom UI widgets
│ └── user/ # User-specific components
├── lib/ # External dependencies
└── resources/
└── icon/ # Application icons

##Installation
**Prerequisites**
  - Java 17 JDK
  - Maven 3.8+
  - Running backend server
