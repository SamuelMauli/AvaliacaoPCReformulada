#!/bin/bash
echo "Compilando o sistema..."
rm -rf bin
mkdir -p bin
javac -d bin src/comum/*.java src/fabrica/*.java src/loja/*.java src/cliente/*.java
if [ $? -eq 0 ]; then
    echo "Compilacao concluida com sucesso!"
else
    echo "Erro na compilacao!"
    exit 1
fi
