# HealthyEat - Health Diet and Nutrition Monitoring System

**COMP2850 Software Engineering - University of Leeds**

## Overview

HealthyEat is a comprehensive web-based health diet and nutrition monitoring system that allows users to track their food intake, browse healthy recipes, read health knowledge articles, explore food calorie data, receive professional advice from health professionals, and engage in real-time chat with assigned nutritionists.

## Tech Stack

- **Backend**: Spring Boot 3.2.0 (Kotlin)
- **Database**: H2 (file-based persistence)
- **Security**: Spring Security + JWT
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle (Kotlin DSL)
- **Frontend**: Bootstrap 5, Chart.js, Font Awesome, vanilla HTML/CSS/JS

## User Roles

| Role | Description |
|------|-------------|
| `SUBSCRIBER` | Regular user who manages food diary, views trends, receives advice, rates and comments on content, and chats with assigned nutritionist |
| `PROFESSIONAL` | Health professional who views client diaries, provides advice, manages content, and chats with clients |
| `ADMIN` | System administrator who manages users, content, and views system statistics |

## Getting Started

### Prerequisites

- Java 17 or higher
- Gradle (or use the included Gradle wrapper)

### Running the Application

```bash
./gradlew bootRun
```

The application will start at `http://localhost:8080`.

### Default Admin Account

| Email | Password | Role |
|-------|----------|------|
| admin@example.com | admin123 | ADMIN |

### H2 Console

Access the H2 database console at `http://localhost:8080/h2-console`:
- **JDBC URL**: `jdbc:h2:file:./data/healthyeat`
- **Username**: `sa`
- **Password**: (leave blank)

## Features

### Public Features (No Login Required)

- **Homepage** - Hero carousel with featured articles, recipe highlights, popular foods
- **Recipes** - Browse, search, and filter healthy recipes with card layout and pagination
- **Recipe Detail** - Full recipe view with ingredients, instructions, rating, and comments
- **Knowledge Base** - Health articles with search, filter, and pagination
- **Food Database** - Searchable calorie database with nutritional information

### Subscriber Features

- **Food Diary** - Add, edit, delete food entries with notes and food autocomplete
- **Calorie Trends** - 7-day calorie trend chart using Chart.js
- **Professional Advice** - View advice from assigned health professionals
- **Rate & Comment** - Rate recipes and articles (1-5 stars), leave comments
- **My Profile** - Edit profile information and change password
- **My Nutritionist** - Assign, view, change, or remove assigned nutritionist
- **Chat Widget** - Floating chat panel to message assigned nutritionist with unread badge and auto-polling

### Professional Features

- **Client Management** - View assigned clients and their food diaries
- **Give Advice** - Provide dietary advice to clients
- **Content Management** - Create and manage recipes, articles, and food entries
- **Chat Widget** - Floating chat panel to message individual clients with unread badge and auto-polling

### Admin Features

- **User Management** - Enable/disable users, change roles, delete users
- **Content Management** - Full CRUD for recipes, knowledge articles, and food database
- **Image Upload** - Upload images for content items
- **System Statistics** - View user counts, content counts, and engagement metrics
- **Professional Stats** - View nutritionist client counts

## API Endpoints

### Public Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/recipes` | Get paginated recipes (search/filter) |
| GET | `/api/recipes/{id}` | Get recipe detail |
| GET | `/api/recipes/{id}/rating` | Get recipe ratings |
| GET | `/api/recipes/{id}/comments` | Get recipe comments |
| GET | `/api/knowledge` | Get paginated articles (search/filter) |
| GET | `/api/knowledge/{id}` | Get article detail |
| GET | `/api/knowledge/latest` | Get latest articles for carousel |
| GET | `/api/knowledge/{id}/rating` | Get article ratings |
| GET | `/api/knowledge/{id}/comments` | Get article comments |
| GET | `/api/food-database` | Get paginated foods (search/filter) |
| GET | `/api/food-database/popular` | Get popular foods |

### Authenticated Endpoints

| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | Public | Login |
| POST | `/api/auth/register` | Public | Register subscriber |
| POST | `/api/auth/register/professional` | Public | Register professional |
| GET | `/api/users/me` | Any | Get current user info |
| PUT | `/api/users/me` | Any | Update profile (name, email) |
| PUT | `/api/users/me/password` | Any | Change password |
| POST | `/api/recipes/{id}/rate` | Any | Rate a recipe |
| POST | `/api/recipes/{id}/comments` | Any | Comment on recipe |
| DELETE | `/api/recipes/{id}/comments/{cid}` | Any | Delete own comment |
| POST | `/api/knowledge/{id}/rate` | Any | Rate an article |
| POST | `/api/knowledge/{id}/comments` | Any | Comment on article |
| DELETE | `/api/knowledge/{id}/comments/{cid}` | Any | Delete own comment |
| GET | `/api/food-entries` | Subscriber | Get food diary |
| POST | `/api/food-entries` | Subscriber | Add food entry |
| PUT | `/api/food-entries/{id}` | Subscriber | Update food entry |
| DELETE | `/api/food-entries/{id}` | Subscriber | Delete food entry |
| GET | `/api/food-entries/trends` | Subscriber | Get calorie trends |
| GET | `/api/advice` | Subscriber | Get professional advice |
| GET | `/api/professional/clients` | Professional | Get client list |
| GET | `/api/professional/clients/{id}/food-entries` | Professional | View client diary |
| POST | `/api/professional/clients/{id}/advice` | Professional | Give advice |
| GET | `/api/professional/clients/{id}/advice` | Professional | Get advice history |
| POST | `/api/messages` | Any | Send chat message |
| GET | `/api/messages/conversation` | Any | Get conversation with other user |
| GET | `/api/messages/unread` | Any | Get unread message count |
| POST | `/api/messages/mark-read` | Any | Mark messages as read |
| GET | `/api/subscribers/professionals` | Subscriber | List available nutritionists |
| GET | `/api/subscribers/my-professional` | Subscriber | Get assigned nutritionist |
| POST | `/api/subscribers/my-professional` | Subscriber | Assign nutritionist |
| DELETE | `/api/subscribers/my-professional` | Subscriber | Remove nutritionist |
| GET | `/api/admin/users` | Admin | Get all users |
| PUT | `/api/admin/users/{id}/enable` | Admin | Enable/disable user |
| PUT | `/api/admin/users/{id}/role` | Admin | Change user role |
| DELETE | `/api/admin/users/{id}` | Admin | Delete user |
| POST | `/api/admin/recipes` | Admin/Professional | Create recipe |
| PUT | `/api/admin/recipes/{id}` | Admin/Professional | Update recipe |
| DELETE | `/api/admin/recipes/{id}` | Admin/Professional | Delete recipe |
| POST | `/api/admin/knowledge` | Admin/Professional | Create article |
| PUT | `/api/admin/knowledge/{id}` | Admin/Professional | Update article |
| DELETE | `/api/admin/knowledge/{id}` | Admin/Professional | Delete article |
| POST | `/api/admin/food-database` | Admin/Professional | Create food entry |
| PUT | `/api/admin/food-database/{id}` | Admin/Professional | Update food entry |
| DELETE | `/api/admin/food-database/{id}` | Admin/Professional | Delete food entry |
| POST | `/api/admin/upload-image` | Admin/Professional | Upload image |

## Frontend Pages

| Page | Path | Access |
|------|------|--------|
| Homepage | `/index.html` | Public |
| Recipes | `/recipes.html` | Public |
| Recipe Detail | `/recipe-detail.html` | Public |
| Knowledge | `/knowledge.html` | Public |
| Knowledge Article | `/knowledge-article.html` | Public |
| Food Database | `/food-database.html` | Public |
| Login | `/login.html` | Public |
| Register | `/register.html` | Public |
| Dashboard | `/dashboard.html` | Subscriber (Profile, Nutritionist, Password) |
| Food Diary | `/food-diary.html` | Subscriber (Diary, Trends, Advice + Chat Widget) |
| Clients | `/clients.html` | Professional (Clients, Recipes, Articles, Foods tabs) |
| Client Diary | `/client-diary.html` | Professional (Food Diary, Advice, Chat Widget) |
| Admin Panel | `/admin.html` | Admin (Users, Recipes, Articles, Foods, Stats tabs) |

## Testing

```bash
./gradlew test
```

## Security

- Passwords hashed using BCrypt
- JWT tokens expire after 24 hours
- Role-based access control via Spring Security
- File upload restricted to admin/professional roles

## WCAG Accessibility

- All forms have properly associated labels
- Bootstrap 5 default color contrast ratios
- All images have alt attributes
- Buttons have clear descriptive text
