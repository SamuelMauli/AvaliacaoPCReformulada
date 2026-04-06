#!/bin/bash
echo "============================================"
echo "  SISTEMA DE PRODUCAO DE VEICULOS"
echo "  Luiz Athar, Pedro Rossi, Samuel Mauli"
echo "============================================"
echo ""

rm -f log_*.txt

echo "[1/3] Iniciando Fabrica..."
java -cp bin fabrica.FabricaMain &
PID_FABRICA=$!
sleep 3

echo "[2/3] Iniciando Lojas..."
java -cp bin loja.LojaMain 1 &
PID_LOJA1=$!
java -cp bin loja.LojaMain 2 &
PID_LOJA2=$!
java -cp bin loja.LojaMain 3 &
PID_LOJA3=$!
sleep 3

echo "[3/3] Iniciando Clientes..."
java -cp bin cliente.ClienteMain
RESULTADO=$?

echo ""
echo "============================================"
echo "  Clientes finalizaram. Encerrando sistema."
echo "============================================"

kill $PID_LOJA1 $PID_LOJA2 $PID_LOJA3 $PID_FABRICA 2>/dev/null
wait 2>/dev/null

echo ""
echo "Logs gerados:"
ls -la log_*.txt 2>/dev/null
echo ""
echo "Sistema encerrado."
