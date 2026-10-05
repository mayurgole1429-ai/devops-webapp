# End-to-End DevOps Pipeline

**Tools:** Git & GitHub (source control) · Jenkins (CI/CD) · Maven (build) · Selenium (testing) · Docker (containerization) · Docker Hub (image registry)

A simple Java (Jakarta Servlet) web application that is built, tested, containerized, and published fully automatically:

```
GitHub → Jenkins (Pipeline)
            ├── Stage 1: Checkout source from GitHub
            ├── Stage 2: Maven build (compile + package WAR)
            ├── Stage 3: Selenium tests against embedded Jetty   ← gates later stages
            ├── Stage 4: Docker image build
            └── Stage 5: Push image to Docker Hub
```

## Project structure

```
├── pom.xml                              # Maven config (Selenium, Jetty, Failsafe plugins)
├── Jenkinsfile                          # Declarative pipeline with 5 stages
├── Dockerfile                           # Multi-stage: Maven build → Tomcat runtime
├── src/main/webapp/index.html           # Home page with greeting form
├── src/main/java/com/devops/GreetServlet.java
└── src/test/java/com/devops/WebAppSeleniumIT.java   # Selenium integration tests
```

The Selenium test automates: opening the app → entering a name → clicking **Greet Me** → verifying the greeting text (plus a default-"Guest" case). `WebDriverManager` downloads the matching ChromeDriver automatically. Maven's `mvn verify` starts embedded Jetty, runs the tests, and stops the server — so the pipeline runs tests with no manual setup.

## 1–3. Create app, Git repo, and Selenium tests — done in this repository

Already implemented and verified: `mvn clean verify` → `BUILD SUCCESS`, `Tests run: 2, Failures: 0`.

Push to GitHub:

```bash
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/devops-webapp.git
git push -u origin main
```

## 4. Configure Jenkins

1. Install Jenkins (LTS), start it (`http://localhost:8080`), unlock with the admin password from `C:\Program Files\Jenkins\secrets\initialAdminPassword`, and install the suggested plugins.
2. **Global tools** — *Manage Jenkins → Tools*:
   - JDK: add `JDK-17` (point to a JDK 17+ install).
   - Maven: add `Maven-3.9` (let Jenkins install it automatically).
3. **Docker Hub credentials** — *Manage Jenkins → Credentials → Global → Add Credentials*:
   - Kind: **Username with password**, ID: `dockerhub-credentials`.
4. The Jenkins node must have **Docker Desktop running** and **Chrome** installed (for Selenium).

## 5–6. Build and test stages

The pipeline's *Build (Maven)* stage runs `mvn clean compile`; the *Test (Selenium)* stage runs `mvn verify`. If any Selenium test fails, the build stops and the Docker stages never run (configured gate).

## 7. Create a new pipeline job

*New Item → Pipeline*, name it `devops-pipeline` → **Pipeline section**:
- Definition: *Pipeline script from SCM*
- SCM: Git, repository URL: `https://github.com/YOUR_GITHUB_USERNAME/devops-webapp.git`, branch: `*/main`
- Script path: `Jenkinsfile`

## 8–9. Docker image + Docker Hub push

In the Jenkinsfile replace:
- `YOUR_DOCKERHUB_USERNAME` with your Docker Hub username
- `https://github.com/YOUR_GITHUB_USERNAME/devops-webapp.git` with your repo URL
- the credentials ID if you used a different one

The *Push to Docker Hub* stage runs `docker login` with the stored credentials and pushes `youruser/devops-webapp:<BUILD_NUMBER>` and `:latest`. Create the public repository `devops-webapp` on [hub.docker.com](https://hub.docker.com) once.

Run the app from the published image:

```bash
docker run -d -p 8080:8080 youruser/devops-webapp:latest
# open http://localhost:8080
```

## Screenshots to capture (results documentation)

1. **GitHub repository** — repo page showing all project files
2. **Jenkins pipeline** — the `devops-pipeline` job showing all 5 stages green
3. **Successful Maven build** — console output with `BUILD SUCCESS`
4. **Selenium test execution** — console output `Tests run: 2, Failures: 0` and the JUnit trend graph
5. **Docker image creation** — console output of `docker build` / `docker images`
6. **Docker Hub repository** — your `devops-webapp` repo page showing the pushed tags

## Conclusion

Each commit pushed to GitHub triggers the pipeline automatically: Jenkins checks out the code, Maven compiles and packages the web application, Selenium validates the UI end-to-end (blocking the pipeline on failure), Docker builds a reproducible image, and the image is published to Docker Hub — demonstrating a complete CI/CD workflow from source code to deployable container.
