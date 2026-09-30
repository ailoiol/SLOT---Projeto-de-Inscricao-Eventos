ORG_ID=4
PART_ID=5

echo "ORG_ID=$ORG_ID (já existente)"
echo "PART_ID=$PART_ID (já existente)"

echo ""
echo "=== 3. TESTE: participante tentando criar evento (esperado: 409) ==="
curl -s -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d "{
    \"organizador\": {\"id\": $PART_ID},
    \"titulo\": \"Evento Inválido\",
    \"dataEvento\": \"2026-12-20T10:00:00\",
    \"vagasTotais\": 10
  }"

echo ""
echo ""
echo "=== 4. Criar evento válido com 1 vaga (organizador de verdade) ==="
EVENTO=$(curl -s -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d "{
    \"organizador\": {\"id\": $ORG_ID},
    \"titulo\": \"Workshop com 1 Vaga\",
    \"dataEvento\": \"2026-12-20T10:00:00\",
    \"vagasTotais\": 1
  }")
echo "$EVENTO"
EVENTO_ID=$(echo "$EVENTO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "EVENTO_ID=$EVENTO_ID"

echo ""
echo "=== 5. Primeira inscrição nesse evento (esperado: sucesso) ==="
INSCRICAO=$(curl -s -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d "{
    \"responsavel\": {\"id\": $PART_ID},
    \"evento\": {\"id\": $EVENTO_ID},
    \"nomeParticipante\": \"Beatriz\",
    \"cpfParticipante\": \"444.444.444-44\",
    \"telefoneParticipante\": \"(61) 96666-5555\"
  }")
echo "$INSCRICAO"
INSCRICAO_ID=$(echo "$INSCRICAO" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "INSCRICAO_ID=$INSCRICAO_ID"

echo ""
echo "=== 6. Segunda inscrição, evento já esgotado (esperado: 409 vagas esgotadas) ==="
curl -s -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d "{
    \"responsavel\": {\"id\": $PART_ID},
    \"evento\": {\"id\": $EVENTO_ID},
    \"nomeParticipante\": \"Outra Pessoa\",
    \"cpfParticipante\": \"555.555.555-55\",
    \"telefoneParticipante\": \"(61) 95555-4444\"
  }"

echo ""
echo ""
echo "=== 7. Cancelar a primeira inscrição (evento está longe no tempo, esperado: sucesso) ==="
curl -s -X DELETE http://localhost:8080/inscricoes/$INSCRICAO_ID

echo ""
echo ""
echo "=== 8. Criar evento daqui a 1h (para testar regra das 2h) ==="
DATA_PROXIMA=$(date -v+1H +"%Y-%m-%dT%H:%M:%S" 2>/dev/null || date -d "+1 hour" +"%Y-%m-%dT%H:%M:%S")
EVENTO2=$(curl -s -X POST http://localhost:8080/eventos \
  -H "Content-Type: application/json" \
  -d "{
    \"organizador\": {\"id\": $ORG_ID},
    \"titulo\": \"Evento em 1 hora\",
    \"dataEvento\": \"$DATA_PROXIMA\",
    \"vagasTotais\": 5
  }")
echo "$EVENTO2"
EVENTO2_ID=$(echo "$EVENTO2" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

echo ""
echo "=== 9. Inscrever no evento próximo ==="
INSCRICAO2=$(curl -s -X POST http://localhost:8080/inscricoes \
  -H "Content-Type: application/json" \
  -d "{
    \"responsavel\": {\"id\": $PART_ID},
    \"evento\": {\"id\": $EVENTO2_ID},
    \"nomeParticipante\": \"Beatriz\",
    \"cpfParticipante\": \"666.666.666-66\",
    \"telefoneParticipante\": \"(61) 94444-3333\"
  }")
echo "$INSCRICAO2"
INSCRICAO2_ID=$(echo "$INSCRICAO2" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')

echo ""
echo "=== 10. TESTE: tentar cancelar com menos de 2h de antecedência (esperado: 409) ==="
curl -s -X DELETE http://localhost:8080/inscricoes/$INSCRICAO2_ID

echo ""
echo ""
echo "=== 11. Login correto (esperado: sucesso, dados do organizador) ==="
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email": "carlos.org@teste.com", "senha": "senha123"}'

echo ""
echo ""
echo "=== 12. Login com senha errada (esperado: 401) ==="
curl -s -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"email": "carlos.org@teste.com", "senha": "senhaerrada"}'

echo ""