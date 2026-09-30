#!/bin/bash

SUFIXO=$(date +%s)
SUFIXO2=${SUFIXO: -2}

echo "=== 1. Criar usuário novo (senha deve ser salva com hash BCrypt agora) ==="
USUARIO=$(curl -s -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d "{
    \"nome\": \"Daniela Teste\",
    \"cpf\": \"555.666.777-$SUFIXO2\",
    \"telefone\": \"(61) 95555-4444\",
    \"email\": \"daniela.$SUFIXO@teste.com\",
    \"senhaHash\": \"minhaSenha123\",
    \"tipo\": \"participante\"
  }")
echo "$USUARIO"
USUARIO_ID=$(echo "$USUARIO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
EMAIL="daniela.$SUFIXO@teste.com"
echo "USUARIO_ID=$USUARIO_ID"

echo ""
echo "=== 2. TESTE: login com a senha correta (esperado: sucesso, dados do usuário) ==="
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"senha\": \"minhaSenha123\"}"

echo ""
echo ""
echo "=== 3. TESTE: login com senha errada (esperado: 401) ==="
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d "{\"email\": \"$EMAIL\", \"senha\": \"senhaErrada\"}"

echo ""
echo ""
echo "=== 4. Criar evento + critério + inscrição para testar o novo DTO ==="
EVENTO=$(curl -s -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "organizador": {"id": 4},
    "titulo": "Evento Teste DTO",
    "dataEvento": "2026-12-30T10:00:00",
    "vagasTotais": 10
  }')
EVENTO_ID=$(echo "$EVENTO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "EVENTO_ID=$EVENTO_ID"

CRITERIO=$(curl -s -X POST http://localhost:8080/criterios-personalizados \
  -H "Content-Type: application/json" \
  -d "{\"evento\": {\"id\": $EVENTO_ID}, \"campo\": \"Alergias\", \"obrigatorio\": false}")
CRITERIO_ID=$(echo "$CRITERIO" | grep -o '"id":[0-9]*' | tail -1 | grep -o '[0-9]*')
echo "CRITERIO_ID=$CRITERIO_ID"

INSCRICAO=$(curl -s -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d "{
    \"inscricao\": {
      \"responsavel\": {\"id\": $USUARIO_ID},
      \"evento\": {\"id\": $EVENTO_ID},
      \"nomeParticipante\": \"Daniela\",
      \"cpfParticipante\": \"888.999.000-$SUFIXO2\",
      \"telefoneParticipante\": \"(61) 95555-4444\"
    },
    \"respostas\": [{\"criterioPersonalizadoId\": $CRITERIO_ID, \"valor\": \"Nenhuma\"}]
  }")
INSCRICAO_ID=$(echo "$INSCRICAO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "INSCRICAO_ID=$INSCRICAO_ID"

echo ""
echo "=== 5. TESTE: GET respostas-criterio NÃO deve mais expor senhaHash/cpf/telefone ==="
curl -s http://localhost:8080/respostas-criterio/inscricao/$INSCRICAO_ID
echo ""