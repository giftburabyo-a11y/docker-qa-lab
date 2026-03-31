# ─────────────────────────────────────────────────────────────────────
# Stage 1: Use the official Maven + JDK 17 image as our base
# This gives us Java 17 and Maven pre-installed.
# ─────────────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17

# ─────────────────────────────────────────────────────────────────────
# Install Google Chrome (required for Selenium UI tests)
# We also install fonts & libraries Chrome needs to run headless.
# ─────────────────────────────────────────────────────────────────────
RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    ca-certificates \
    fonts-liberation \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libdbus-1-3 \
    libgdk-pixbuf2.0-0 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxrandr2 \
    xdg-utils \
    --no-install-recommends \
    # Add Google's APT key and repository
    && wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" \
       > /etc/apt/sources.list.d/google-chrome.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable --no-install-recommends \
    # Clean up to keep image size small
    && rm -rf /var/lib/apt/lists/*

# ─────────────────────────────────────────────────────────────────────
# Set the working directory inside the container
# ─────────────────────────────────────────────────────────────────────
WORKDIR /app

# ─────────────────────────────────────────────────────────────────────
# Copy pom.xml first and download dependencies (Docker layer caching:
# dependencies are only re-downloaded when pom.xml changes)
# ─────────────────────────────────────────────────────────────────────
COPY pom.xml .
RUN mvn dependency:go-offline -B

# ─────────────────────────────────────────────────────────────────────
# Copy the test source code into the container
# ─────────────────────────────────────────────────────────────────────
COPY src ./src

# ─────────────────────────────────────────────────────────────────────
# Default command: run all tests when the container starts
# -B = batch mode (no colour output, cleaner logs in CI)
# ─────────────────────────────────────────────────────────────────────
CMD ["mvn", "test", "-B"]