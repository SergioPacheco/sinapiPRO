#!/bin/bash
# =============================================================
# SinapiPRO — Script de Deploy Local
# =============================================================
# Uso:
#   ./run.sh          → inicia em background (porta 8090)
#   ./run.sh stop     → para o servidor
#   ./run.sh restart  → para e reinicia
#   ./run.sh logs     → mostra logs em tempo real
#   ./run.sh build    → compila e empacota sem rodar
#   ./run.sh status   → verifica se está rodando
# =============================================================

APP_NAME="sinapiPRO"
JAR="target/sinapiPRO-1.0.0-SNAPSHOT.jar"
LOG="/tmp/sinapipro.log"
PID_FILE="/tmp/sinapipro.pid"
PROFILE="dev"
PORT="8090"
DB_NAME="sinapipro"
DB_USER="sinapipro"
DB_PASS="sinapipro123"

# Cores
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'

check_db() {
    if ! mysql -u "$DB_USER" -p"$DB_PASS" "$DB_NAME" -e "SELECT 1;" &>/dev/null; then
        echo -e "${YELLOW}⚠ Banco de dados não acessível. Criando...${NC}"
        sudo -u mysql mysql << SQL 2>/dev/null || true
CREATE DATABASE IF NOT EXISTS $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS '$DB_USER'@'localhost' IDENTIFIED BY '$DB_PASS';
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'localhost';
FLUSH PRIVILEGES;
SQL
        echo -e "${GREEN}✓ Banco criado: $DB_NAME${NC}"
    fi
}

build() {
    echo -e "${YELLOW}▶ Compilando...${NC}"
    ./mvnw package -DskipTests -q 2>&1
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ Build OK: $JAR${NC}"
    else
        echo -e "${RED}✗ Build falhou. Verifique os erros acima.${NC}"
        exit 1
    fi
}

start() {
    if [ -f "$PID_FILE" ] && kill -0 "$(cat $PID_FILE)" 2>/dev/null; then
        echo -e "${YELLOW}⚠ Já está rodando (PID $(cat $PID_FILE)). Use './run.sh restart' para reiniciar.${NC}"
        return
    fi

    check_db

    if [ ! -f "$JAR" ]; then
        echo -e "${YELLOW}JAR não encontrado. Compilando...${NC}"
        build
    fi

    echo -e "${YELLOW}▶ Iniciando $APP_NAME na porta $PORT...${NC}"
    nohup java -jar "$JAR" \
        --spring.profiles.active="$PROFILE" \
        --server.port="$PORT" \
        > "$LOG" 2>&1 &

    echo $! > "$PID_FILE"
    echo -e "${GREEN}✓ Iniciado (PID $!)${NC}"
    echo -e "  → Aguardando startup..."

    # Aguarda até 30s pelo startup
    for i in $(seq 1 30); do
        sleep 1
        if grep -q "Started SinapiProApplication" "$LOG" 2>/dev/null; then
            echo -e "${GREEN}✓ Sistema disponível em: http://localhost:$PORT${NC}"
            echo -e "  → Login padrão: admin / admin"
            echo -e "  → Logs: $LOG"
            return
        fi
        if grep -q "APPLICATION FAILED\|BeanCreationException\|APPLICATION FAILED TO START" "$LOG" 2>/dev/null; then
            echo -e "${RED}✗ Erro na inicialização. Veja os logs:${NC}"
            tail -20 "$LOG"
            return 1
        fi
        printf "."
    done
    echo -e "\n${YELLOW}⚠ Timeout aguardando startup. Verifique: tail -f $LOG${NC}"
}

stop() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if kill -0 "$PID" 2>/dev/null; then
            kill "$PID"
            rm -f "$PID_FILE"
            echo -e "${GREEN}✓ $APP_NAME parado (PID $PID)${NC}"
        else
            echo -e "${YELLOW}⚠ Processo não encontrado. Limpando PID file.${NC}"
            rm -f "$PID_FILE"
        fi
    else
        # Tenta matar por nome
        pkill -f "sinapiPRO.*jar" 2>/dev/null && echo -e "${GREEN}✓ $APP_NAME parado${NC}" || echo -e "${YELLOW}⚠ $APP_NAME não estava rodando${NC}"
    fi
}

status() {
    if [ -f "$PID_FILE" ] && kill -0 "$(cat $PID_FILE)" 2>/dev/null; then
        echo -e "${GREEN}✓ $APP_NAME está rodando (PID $(cat $PID_FILE))${NC}"
        echo -e "  → http://localhost:$PORT"
    else
        echo -e "${RED}✗ $APP_NAME não está rodando${NC}"
    fi
}

case "${1:-start}" in
    start)   start ;;
    stop)    stop ;;
    restart) stop; sleep 2; start ;;
    logs)    tail -f "$LOG" ;;
    build)   build ;;
    status)  status ;;
    *)
        echo "Uso: $0 {start|stop|restart|logs|build|status}"
        exit 1
        ;;
esac
