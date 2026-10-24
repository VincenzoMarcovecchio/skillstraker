# SkillTrack - Java JSP Template Engine with MySQL CRUD (Render-Ready)

A clean, modern practice tracker web application built with **pure Java Servlets and JSP (.jsp) templates** (no Spring Boot), backed by **MySQL** with HikariCP connection pooling, and designed to match the **SkillTrack** UI.

Hosted seamlessly on **[Render](https://render.com/)** using Docker and Apache Tomcat 10.1.

---

## 🎨 UI Design & Features

Designed to match the SkillTrack dashboard:
- **Greeting & Live Counter**: *"Good afternoon, Jordan — You've practiced 3 skills today. Keep it up!"*
- **Most Practiced Skill Card**:
  - `MOST PRACTICED` badge
  - Dynamic skill name (e.g. **Spanish**)
  - SVG Circular Donut Progress Ring showing `% of weekly goal`
  - Two-column stats: `Total hours` (e.g., `47.5h`) and `Day streak` (🔥 `12`)
  - Subtext: *"Last session: Yesterday — 45 min"*
- **Top Mini Cards**:
  - 🟣 **Guitar**: `32h` (🔥 `8-day streak`)
  - 🔵 **TypeScript**: `18h` (🔥 `3-day streak`)
  - 🔴 **Cooking**: `12h` (`5-day streak`)
- **Practice Activity Heatmap**:
  - GitHub-style 18-week contribution grid (126 cells)
  - Color-coded intensity levels (`Less` 🟩 🟩 🟩 🟩 `More`)
  - Hover tooltips showing exact date and minutes practiced
- **Recent Sessions & Complete CRUD**:
  - List of recent sessions with skill color dot, notes, duration, and relative date (`Today`, `Yesterday`)
  - **Create**: Click `+ Log Session` modal to add practice with quick time pills (`15m`, `30m`, `45m`, `60m`, `90m`)
  - **Read**: Dashboard view + `/sessions` full history with filter-by-skill
  - **Update**: Edit modal for any logged session
  - **Delete**: Safe delete action with prompt confirmation
- **Skills Management (`/skills`)**:
  - Full CRUD for skills: add new skills, customize color picker hex codes, and set weekly hour targets.

---

## 🛠️ Architecture & Tech Stack

- **Template Engine**: JavaServer Pages (**JSP 3.1 / Jakarta JSTL 3.0**)
- **Backend Controller**: Pure Java Servlets (**Jakarta Servlet 6.0**) — **Zero Spring Boot**
- **Persistence**: **JDBC** with **HikariCP** connection pool
- **Database**: **MySQL 8.x** (compatible with MariaDB, Render MySQL, Aiven, TiDB, Clever Cloud)
- **Servlet Container / Server**: **Apache Tomcat 10.1** (runs via Docker on Render or standalone)
- **Build Tool**: Maven (packaging as standard `ROOT.war`)

---

## 📂 Project Structure

```text
news/
├── pom.xml                               # Pure Java Jakarta EE Maven descriptor
├── Dockerfile                            # Multi-stage build (Maven -> Tomcat 10.1)
├── render.yaml                           # Render Blueprint Infrastructure-as-Code
├── README.md                             # Documentation & setup guide
├── db/
│   └── init.sql                          # MySQL schema & initial seed data
└── src/
    └── main/
        ├── java/
        │   └── com/skilltrack/
        │       ├── config/
        │       │   ├── DBConnection.java       # HikariCP pool + Render env parser
        │       │   └── AppContextListener.java # Auto-creates schema & seeds demo data
        │       ├── dao/
        │       │   ├── SkillDAO.java           # Skill CRUD & streak/hours calculator
        │       │   └── SessionDAO.java         # Session CRUD & 18-week heatmap aggregator
        │       ├── model/
        │       │   ├── Skill.java              # Java Bean for Skill
        │       │   └── PracticeSession.java    # Java Bean for Practice Session
        │       ├── dto/
        │       │   ├── DashboardStats.java     # View model for Dashboard
        │       │   ├── SkillStat.java          # Aggregated statistics per skill
        │       │   └── HeatmapDay.java         # Heatmap cell state & tooltips
        │       └── servlet/
        │           ├── DashboardServlet.java   # GET / and /dashboard -> dashboard.jsp
        │           ├── SessionServlet.java     # CRUD endpoints for sessions
        │           └── SkillServlet.java       # CRUD endpoints for skills
        └── webapp/
            ├── WEB-INF/
            │   ├── web.xml                     # Standard Servlet deployment descriptor
            │   └── jsp/
            │       ├── dashboard.jsp           # Main SkillTrack dashboard
            │       ├── sessions.jsp            # Full sessions CRUD page
            │       ├── skills.jsp              # Full skills CRUD page
            │       └── common/
            │           ├── header.jsp          # Navbar & background squiggles/dots
            │           ├── footer.jsp          # Toast messages & footer
            │           └── session_modal.jsp   # Log session & Edit session modals
            └── static/
                ├── css/
                │   └── style.css               # Exact styling matching the design
                └── js/
                    └── app.js                  # Modals, duration selector & helpers
```

---

## 🚀 How to Run Locally

### Prerequisites
- **JDK 17+**
- **Maven 3.8+**
- **MySQL** running locally (e.g. via XAMPP, Docker, or native MySQL server)

### 1. Start MySQL
Ensure MySQL is running on port `3306`.
If using **XAMPP**:
1. Open XAMPP Control Panel.
2. Click **Start** on MySQL.
3. Database `skilltrack` will be automatically created on application startup, or you can import `db/init.sql` in phpMyAdmin.

If using **Docker MySQL**:
```bash
docker run -d --name mysql-skilltrack -p 3306:3306 -e MYSQL_ROOT_PASSWORD= -e MYSQL_DATABASE=skilltrack mysql:8
```

### 2. Build & Run
You can run locally with Maven and Tomcat or build the Docker image:

#### Option A: Docker (Fastest, exactly like Render)
```bash
docker build -t skilltrack .
docker run -p 8080:8080 -e MYSQL_URL="jdbc:mysql://host.docker.internal:3306/skilltrack?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC" -e MYSQL_USER="root" -e MYSQL_PASSWORD="" skilltrack
```
Open [http://localhost:8080](http://localhost:8080) in your browser.

#### Option B: Local Tomcat
```bash
mvn clean package
```
Copy `target/ROOT.war` into your local Tomcat's `webapps/` directory and start Tomcat.

---

## ☁️ Deploying on Render (Step-by-Step)

Render makes hosting Dockerized Java JSP web apps completely automatic.

### Step 1: Push Project to GitHub
1. Create a new repository on GitHub:
   ```bash
   git init
   git add .
   git commit -m "SkillTrack JSP web app with MySQL CRUD"
   git remote add origin https://github.com/<your-username>/skilltrack.git
   git branch -M main
   git push -u origin main
   ```

### Step 2: Set Up a Free MySQL Database
You can use any cloud MySQL provider:
- **[Aiven](https://aiven.io/)** (Free tier MySQL)
- **[Clever Cloud](https://www.clever-cloud.com/)** (Free MySQL add-on)
- **[TiDB Cloud](https://tidbcloud.com/)** (Free Serverless MySQL-compatible database)
- **Render MySQL Plugin**

Copy the connection URL (e.g., `mysql://user:password@host:port/dbname` or JDBC URL).

### Step 3: Create Web Service on Render
1. Log in to [dashboard.render.com](https://dashboard.render.com/).
2. Click **New +** -> **Web Service**.
3. Connect your GitHub repository.
4. Render will auto-detect the `Dockerfile`:
   - **Environment**: `Docker`
   - **Region**: Closest to you (e.g., Oregon, Frankfurt)
   - **Branch**: `main`
   - **Plan**: `Free`
5. Under **Environment Variables**, add:
   - `MYSQL_URL`: Your cloud MySQL JDBC URL (e.g. `jdbc:mysql://<host>:3306/<database>?useSSL=true&serverTimezone=UTC`)
   - `MYSQL_USER`: Your database username
   - `MYSQL_PASSWORD`: Your database password
6. Click **Create Web Service**.

Render will automatically build the multi-stage Docker image, install Tomcat 10.1, deploy `ROOT.war`, configure the dynamic `$PORT`, and launch your application!

---

## 🔌 CRUD Endpoints & Navigation

| Route | Method | Description |
|---|---|---|
| `/` or `/dashboard` | `GET` | Main Dashboard matching the screenshot design |
| `/sessions` | `GET` | View all logged sessions (filterable by skill) |
| `/sessions/create` | `POST` | Log a new practice session |
| `/sessions/edit` | `POST` | Update an existing practice session |
| `/sessions/delete?id={id}` | `GET/POST`| Delete a practice session |
| `/skills` | `GET` | Manage skills, weekly goals, and color themes |
| `/skills/create` | `POST` | Add a new skill |
| `/skills/edit` | `POST` | Update an existing skill |
| `/skills/delete?id={id}` | `GET/POST`| Delete a skill and its history |

---

## 🛡️ Automatic Schema & Demo Data Initialization
When you start the application connected to any fresh MySQL instance:
1. `AppContextListener` verifies the connection.
2. Executes `CREATE TABLE IF NOT EXISTS` for `skills` and `practice_sessions`.
3. If empty, automatically seeds:
   - **Spanish** (`#00875A`, 8h weekly target)
   - **Guitar** (`#7C3AED`, 5h weekly target)
   - **TypeScript** (`#2563EB`, 6h weekly target)
   - **Cooking** (`#DC2626`, 4h weekly target)
   - 18 weeks of realistic practice activity populating the heatmap graph and statistics exactly as shown in the design image!
