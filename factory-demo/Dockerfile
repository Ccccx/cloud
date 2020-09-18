FROM java:8
ARG JAR
COPY target/${JAR} /usr/local/apps/app.jar
ENTRYPOINT  ["java", "-server", "-Xmx256M", "-Xms256M", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=20", "-XX:InitiatingHeapOccupancyPercent=35", "-XX:+DisableExplicitGC", "-jar", "/usr/local/apps/app.jar"]