#!/bin/bash

SUFIXO=$(date +%s)
SUFIXO2=${SUFIXO: -2}

echo "=== 1. Criar evento novo para este teste ==="
EVENTO=$(curl -s -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "organizador": {"id": 4},
    "titulo": "Evento com Critério Obrigatório",
    "dataEvento": "2026-12-25T14:00:00",
    "vagasTotais": 10
  }')
echo "$EVENTO"
EVENTO_ID=$(echo "$EVENTO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "EVENTO_ID=$EVENTO_ID"

echo ""
echo "=== 2. Criar critério obrigatório para esse evento ==="
CRITERIO=$(curl -s -X POST http://localhost:8080/criterios-personalizados \
  -H "Content-Type: application/json" \
  -d "{
    \"evento\": {\"id\": $EVENTO_ID},
    \"campo\": \"Tamanho da camiseta\",
    \"obrigatorio\": true
  }")
echo "$CRITERIO"
CRITERIO_ID=$(echo "$CRITERIO" | grep -o '"id":[0-9]*' | tail -1 | grep -o '[0-9]*')
echo "=== 3. TESTE: inscrever SEM responder o critério obrigatório (esperado: 409) ==="
curl -s -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d "{
    \"inscricao\": {
      \"responsavel\": {\"id\": 5},
      \"evento\": {\"id\": $EVENTO_ID},
      \"nomeParticipante\": \"Teste Sem Resposta\",
      \"cpfParticipante\": \"111.222.333-$SUFIXO2\",
      \"telefoneParticipante\": \"(61) 91111-0000\"
    },
    \"respostas\": []
  }"

echo ""
echo ""
echo "=== 4. TESTE: inscrever COM a resposta do critério obrigatório (esperado: sucesso) ==="
INSCRICAO=$(curl -s -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d "{
    \"inscricao\": {
      \"responsavel\": {\"id\": 5},
      \"evento\": {\"id\": $EVENTO_ID},
      \"nomeParticipante\": \"Teste Com Resposta\",
      \"cpfParticipante\": \"222.333.444-$SUFIXO2\",
      \"telefoneParticipante\": \"(61) 92222-1111\"
    },
    \"respostas\": [
      {\"criterioPersonalizadoId\": $CRITERIO_ID, \"valor\": \"M\"}
    ]
  }")
echo "$INSCRICAO"
INSCRICAO_ID=$(echo "$INSCRICAO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "INSCRICAO_ID=$INSCRICAO_ID"

echo ""
echo "=== 5. Conferir que a resposta foi salva de fato ==="
curl -s http://localhost:8080/respostas-criterio/inscricao/$INSCRICAO_ID
echo ""