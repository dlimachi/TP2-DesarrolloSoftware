#!/bin/bash

# Script para ejecutar Parking Management API en modo desarrollo
# Uso: ./run.sh

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Ensure Maven uses Java 21
JAVA_HOME=$(/usr/libexec/java_home -v 21 2>/dev/null)
if [ -z "$JAVA_HOME" ]; then
    echo -e "${RED}Error: Java 21 no encontrado.${NC}"
    exit 1
fi
export JAVA_HOME

PORT=8081

echo -e "${BLUE}=== Parking Management API (dev) ===${NC}"
echo ""
echo -e "${BLUE}🚀 Iniciando aplicación...${NC}"
echo -e "${GREEN}API:        http://localhost:${PORT}/api${NC}"
echo -e "${GREEN}H2 Console: http://localhost:${PORT}/api/h2-console${NC}"
echo ""
echo -e "${YELLOW}Usuario de prueba:${NC}"
echo -e "  Email:    laura.fernandez@outlook.com"
echo -e "  Password: password123"
echo ""
echo -e "${YELLOW}Presiona Ctrl+C para detener${NC}"
echo ""

mvn spring-boot:run -Dspring-boot.run.profiles=dev