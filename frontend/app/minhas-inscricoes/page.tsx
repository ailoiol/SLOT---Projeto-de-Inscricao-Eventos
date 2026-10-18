"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import type { Inscricao, Usuario } from "../types";

export default function MinhasInscricoesPage() {
  const router = useRouter();
  const [inscricoes, setInscricoes] = useState<Inscricao[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  useEffect(() => {
    async function carregar() {
      const salvo = localStorage.getItem("usuario");
      if (!salvo) {
        router.push("/login");
        return;
      }
      const usuario: Usuario = JSON.parse(salvo);

      const resposta = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/inscricoes?responsavelId=${usuario.id}`,
      );
      if (resposta.ok) {
        setInscricoes(await resposta.json());
      }
      setCarregando(false);
    }
    carregar();
  }, [router]);

  async function cancelar(id: number) {
    if (!confirm("Deseja realmente cancelar esta inscrição?")) {
      return;
    }
    setErro("");

    const resposta = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/inscricoes/${id}`,
      {
        method: "DELETE",
      },
    );

    if (!resposta.ok) {
      const corpo = await resposta.json();
      setErro(corpo.erro ?? "Não foi possível cancelar a inscrição.");
      return;
    }

    const atualizada: Inscricao = await resposta.json();
    setInscricoes(
      inscricoes.map((i) => (i.id === atualizada.id ? atualizada : i)),
    );
  }

  if (carregando) {
    return (
      <main className="p-8">
        <p>Carregando...</p>
      </main>
    );
  }

  return (
    <main className="p-8">
      <h1 className="text-3xl font-bold">Minhas inscrições</h1>

      {erro && <p className="mt-4 text-red-600">{erro}</p>}

      {inscricoes.length === 0 ? (
        <p className="mt-6">Você ainda não tem inscrições.</p>
      ) : (
        <ul className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {inscricoes.map((inscricao) => (
            <li key={inscricao.id} className="rounded-lg border p-4 shadow">
              <h2 className="text-xl font-bold">
                <Link href={`/eventos/${inscricao.evento.id}`}>
                  {inscricao.evento.titulo}
                </Link>
              </h2>
              <p>
                Data:{" "}
                {new Date(inscricao.evento.dataEvento).toLocaleString("pt-BR", {
                  dateStyle: "short",
                  timeStyle: "short",
                })}
              </p>
              <p>Participante: {inscricao.nomeParticipante || "Sem nome"}</p>
              <p>Status: {inscricao.status}</p>

              {inscricao.status === "ativa" && (
                <button
                  onClick={() => cancelar(inscricao.id)}
                  className="mt-3 rounded border border-red-600 px-3 py-1 text-red-600"
                >
                  Cancelar inscrição
                </button>
              )}
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
