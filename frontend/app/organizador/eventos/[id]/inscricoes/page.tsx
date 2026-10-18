"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import type {
  Criterio,
  Evento,
  Inscricao,
  RespostaCriterio,
  Usuario,
} from "../../../../types";

export default function InscritosPage() {
  const router = useRouter();
  const params = useParams<{ id: string }>();
  const eventoId = Number(params.id);

  const [evento, setEvento] = useState<Evento | null>(null);
  const [inscricoes, setInscricoes] = useState<Inscricao[]>([]);
  const [criterios, setCriterios] = useState<Criterio[]>([]);
  const [respostas, setRespostas] = useState<RespostaCriterio[]>([]);

  useEffect(() => {
    async function carregar() {
      const salvo = localStorage.getItem("usuario");
      if (!salvo) {
        router.push("/login");
        return;
      }
      const logado: Usuario = JSON.parse(salvo);
      if (logado.tipo !== "organizador") {
        router.push("/eventos");
        return;
      }

      const respostaEvento = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/eventos/${eventoId}`,
      );
      if (!respostaEvento.ok) {
        router.push("/organizador/eventos");
        return;
      }
      const dadosEvento: Evento = await respostaEvento.json();

      if (dadosEvento.organizador.id !== logado.id) {
        router.push("/organizador/eventos");
        return;
      }

      const [respInscricoes, respCriterios, respRespostas] = await Promise.all([
        fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/inscricoes?eventoId=${eventoId}`,
        ),
        fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/criterios-personalizados?eventoId=${eventoId}`,
        ),
        fetch(
          `${process.env.NEXT_PUBLIC_API_URL}/respostas-criterio?eventoId=${eventoId}`,
        ),
      ]);

      if (respInscricoes.ok) setInscricoes(await respInscricoes.json());
      if (respCriterios.ok) setCriterios(await respCriterios.json());
      if (respRespostas.ok) setRespostas(await respRespostas.json());

      setEvento(dadosEvento);
    }
    carregar();
  }, [router, eventoId]);

  function valorDaResposta(inscricaoId: number, criterioId: number) {
    const resposta = respostas.find(
      (r) =>
        r.inscricaoId === inscricaoId &&
        r.criterioPersonalizadoId === criterioId,
    );
    return resposta?.valor ?? "—";
  }

  if (!evento) {
    return (
      <main className="p-8">
        <p>Carregando...</p>
      </main>
    );
  }

  const ativas = inscricoes.filter((i) => i.status === "ativa").length;

  return (
    <main className="p-8">
      <Link href="/organizador/eventos" className="underline">
        ← Meus eventos
      </Link>

      <h1 className="mt-4 text-3xl font-bold">Inscritos: {evento.titulo}</h1>
      <p className="mt-2">
        Inscrições ativas: {ativas} (vagas ocupadas: {evento.vagasOcupadas}/
        {evento.vagasTotais})
      </p>

      {inscricoes.length === 0 ? (
        <p className="mt-6">Nenhuma inscrição neste evento ainda.</p>
      ) : (
        <div className="mt-6 overflow-x-auto">
          <table className="w-full border-collapse text-left">
            <thead>
              <tr className="border-b">
                <th className="p-2">Participante</th>
                <th className="p-2">CPF</th>
                <th className="p-2">Telefone</th>
                {criterios.map((criterio) => (
                  <th key={criterio.id} className="p-2">
                    {criterio.campo}
                  </th>
                ))}
                <th className="p-2">Conta responsável</th>
                <th className="p-2">Status</th>
              </tr>
            </thead>
            <tbody>
              {inscricoes.map((inscricao) => (
                <tr
                  key={inscricao.id}
                  className={
                    inscricao.status === "cancelada"
                      ? "border-b text-gray-500"
                      : "border-b"
                  }
                >
                  <td className="p-2">
                    {inscricao.nomeParticipante || "Sem nome"}
                  </td>
                  <td className="p-2">{inscricao.cpfParticipante || "—"}</td>
                  <td className="p-2">
                    {inscricao.telefoneParticipante || "—"}
                  </td>
                  {criterios.map((criterio) => (
                    <td key={criterio.id} className="p-2">
                      {valorDaResposta(inscricao.id, criterio.id)}
                    </td>
                  ))}
                  <td className="p-2">{inscricao.usuario.nome}</td>
                  <td className="p-2">{inscricao.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </main>
  );
}
