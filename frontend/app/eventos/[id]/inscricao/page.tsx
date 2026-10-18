"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useParams, useRouter } from "next/navigation";
import type { Criterio, Usuario } from "../../../types";

export default function InscricaoPage() {
  const router = useRouter();
  const params = useParams<{ id: string }>();
  const eventoId = Number(params.id);

  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");
  const [telefone, setTelefone] = useState("");
  const [criterios, setCriterios] = useState<Criterio[]>([]);
  const [respostas, setRespostas] = useState<Record<number, string>>({});
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState(false);

  useEffect(() => {
    async function carregarCriterios() {
      const resposta = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/criterios-personalizados?eventoId=${eventoId}`,
      );
      if (resposta.ok) {
        setCriterios(await resposta.json());
      }
    }
    carregarCriterios();
  }, [eventoId]);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro("");

    const salvo = localStorage.getItem("usuario");
    if (!salvo) {
      router.push("/login");
      return;
    }
    const usuario: Usuario = JSON.parse(salvo);

    const respostasParaEnviar = criterios
      .filter((criterio) => respostas[criterio.id])
      .map((criterio) => ({
        criterioPersonalizadoId: criterio.id,
        valor: respostas[criterio.id],
      }));

    const resposta = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/inscricoes`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          inscricao: {
            responsavel: { id: usuario.id },
            evento: { id: eventoId },
            nomeParticipante: nome,
            cpfParticipante: cpf,
            telefoneParticipante: telefone,
          },
          respostas: respostasParaEnviar,
        }),
      },
    );

    if (!resposta.ok) {
      const corpo = await resposta.json();
      setErro(corpo.erro ?? "Não foi possível realizar a inscrição.");
      return;
    }

    setSucesso(true);
  }

  if (sucesso) {
    return (
      <main className="mx-auto max-w-sm p-8">
        <h1 className="text-3xl font-bold">Inscrição realizada!</h1>
        <Link href="/eventos" className="mt-4 inline-block underline">
          Voltar ao mural
        </Link>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-sm p-8">
      <h1 className="text-3xl font-bold">Inscrição</h1>

      <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4">
        <label className="flex flex-col gap-1">
          Nome do participante
          <input
            type="text"
            className="rounded border p-2"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          CPF do participante
          <input
            type="text"
            className="rounded border p-2"
            value={cpf}
            onChange={(e) => setCpf(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Telefone (opcional)
          <input
            type="tel"
            className="rounded border p-2"
            value={telefone}
            onChange={(e) => setTelefone(e.target.value)}
          />
        </label>

        {criterios.map((criterio) => (
          <label key={criterio.id} className="flex flex-col gap-1">
            {criterio.campo}
            {criterio.obrigatorio ? " *" : " (opcional)"}
            <input
              type="text"
              className="rounded border p-2"
              required={criterio.obrigatorio}
              value={respostas[criterio.id] ?? ""}
              onChange={(e) =>
                setRespostas({ ...respostas, [criterio.id]: e.target.value })
              }
            />
          </label>
        ))}

        <button
          type="submit"
          className="rounded bg-blue-600 p-2 font-medium text-white"
        >
          Confirmar inscrição
        </button>
      </form>

      {erro && <p className="mt-4 text-red-600">{erro}</p>}
    </main>
  );
}
