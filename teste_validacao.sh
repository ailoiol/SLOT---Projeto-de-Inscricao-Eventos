#!/bin/bash

SUFIXO=$(date +%s)
SUFIXO2=${SUFIXO: -2}

echo "=== 1. TESTE: criar usuário SEM email (esperado: 400 com mensagem clara) ==="
curl -s -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Sem Email",
    "cpf": "111.111.111-11",
    "telefone": "(61) 91111-1111",
    "senhaHash": "senha123",
    "tipo": "participante"
  }'

echo ""
echo ""
echo "=== 2. TESTE: criar usuário com email INVÁLIDO (esperado: 400) ==="
curl -s -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d "{
    \"nome\": \"Email Invalido\",
    \"cpf\": \"222.222.222-$SUFIXO2\",
    \"telefone\": \"(61) 92222-2222\",
    \"email\": \"nao-e-um-email\",
    \"senhaHash\": \"senha123\",
    \"tipo\": \"participante\"
  }"

echo ""
echo ""
echo "=== 3. TESTE: criar evento SEM título (esperado: 400) ==="
curl -s -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "organizador": {"id": 4},
    "dataEvento": "2026-12-30T10:00:00",
    "vagasTotais": 10
  }'

echo ""
echo ""
echo "=== 4. TESTE: criar evento com vagasTotais NEGATIVO (esperado: 400) ==="
curl -s -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "organizador": {"id": 4},
    "titulo": "Evento com vaga negativa",
    "dataEvento": "2026-12-30T10:00:00",
    "vagasTotais": -5
  }'

echo ""
echo ""
echo "=== 5. TESTE: criar inscrição SEM nomeParticipante (esperado: 400, validando o DTO aninhado) ==="
curl -s -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d "{
    \"inscricao\": {
      \"responsavel\": {\"id\": 5},
      \"evento\": {\"id\": 4},
      \"cpfParticipante\": \"333.333.333-$SUFIXO2\",
      \"telefoneParticipante\": \"(61) 93333-3333\"
    },
    \"respostas\": []
  }"

echo ""
echo ""
echo "=== 6. Controle: criar usuário VÁLIDO (esperado: sucesso, sem erros de validação) ==="
USUARIO=$(curl -s -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d "{
    \"nome\": \"Usuario Valido\",
    \"cpf\": \"444.444.444-$SUFIXO2\",
    \"telefone\": \"(61) 94444-4444\",
    \"email\": \"valido.$SUFIXO@teste.com\",
    \"senhaHash\": \"senhaOriginal123\",
    \"tipo\": \"participante\"
  }")
echo "$USUARIO"
USUARIO_ID=$(echo "$USUARIO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "USUARIO_ID=$USUARIO_ID"

echo ""
echo "=== 7. TESTE: login com a senha original (esperado: sucesso) ==="
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"valido.$SUFIXO@teste.com\", \"senha\": \"senhaOriginal123\"}"

echo ""
echo ""
echo "=== 8. Atualizar esse usuário com uma senha NOVA via PUT ==="
curl -s -X PUT http://localhost:8080/usuarios/$USUARIO_ID \
  -H "Content-Type: application/json" \
  -d "{
    \"nome\": \"Usuario Valido\",
    \"cpf\": \"444.444.444-$SUFIXO2\",
    \"telefone\": \"(61) 94444-4444\",
    \"email\": \"valido.$SUFIXO@teste.com\",
    \"senhaHash\": \"senhaNova456\",
    \"tipo\": \"participante\",
    \"perfilCompleto\": false
  }"

echo ""
echo ""
echo "=== 9. TESTE: login com a senha NOVA (esperado: sucesso, prova que o PUT re-encriptou) ==="
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"valido.$SUFIXO@teste.com\", \"senha\": \"senhaNova456\"}"

echo ""
echo ""
echo "=== 10. TESTE: login com a senha ANTIGA (esperado: 401, prova que a antiga não funciona mais) ==="
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"valido.$SUFIXO@teste.com\", \"senha\": \"senhaOriginal123\"}"

echo ""