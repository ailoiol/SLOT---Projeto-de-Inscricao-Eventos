"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import type { Evento, Usuario } from "../../../../types";

export default function EditarEventoPage() {
  const router = useRouter();
  const params = useParams<{ id: string }>();
  const eventoId = Number(params.id);

  const [usuario, setUsuario] = useState<Usuario | null>(null);
  const [evento, setEvento] = useState<Evento | null>(null);
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [dataEvento, setDataEvento] = useState("");
  const [vagas, setVagas] = useState("");
  const [status, setStatus] = useState("aberto");
  const [erro, setErro] = useState("");

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

      const resposta = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/eventos/${eventoId}`,
      );
      if (!resposta.ok) {
        router.push("/organizador/eventos");
        return;
      }
      const dados: Evento = await resposta.json();

      if (dados.organizador.id !== logado.id) {
        router.push("/organizador/eventos");
        return;
      }

      setUsuario(logado);
      setEvento(dados);
      setTitulo(dados.titulo);
      setDescricao(dados.descricao ?? "");
      setDataEvento(dados.dataEvento.slice(0, 16));
      setVagas(String(dados.vagasTotais));
      setStatus(dados.status);
    }
    carregar();
  }, [router, eventoId]);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro("");
    if (!usuario || !evento) return;

    const resposta = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/eventos/${eventoId}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          organizador: { id: usuario.id },
          titulo,
          descricao: descricao || null,
          dataEvento,
          vagasTotais: Number(vagas),
          vagasOcupadas: evento.vagasOcupadas,
          status,
        }),
      },
    );

    if (!resposta.ok) {
      const corpo = await resposta.json();
      setErro(corpo.erro ?? "Não foi possível salvar as alterações.");
      return;
    }

    router.push("/organizador/eventos");
  }

  if (!usuario || !evento) {
    return (
      <main className="p-8">
        <p>Carregando...</p>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-sm p-8">
      <h1 className="text-3xl font-bold">Editar evento</h1>

      <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4">
        <label className="flex flex-col gap-1">
          Título
          <input
            type="text"
            required
            className="rounded border p-2"
            value={titulo}
            onChange={(e) => setTitulo(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Descrição (opcional)
          <textarea
            className="rounded border p-2"
            rows={3}
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Data e hora
          <input
            type="datetime-local"
            required
            className="rounded border p-2"
            value={dataEvento}
            onChange={(e) => setDataEvento(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Número de vagas (já ocupadas: {evento.vagasOcupadas})
          <input
            type="number"
            min={Math.max(1, evento.vagasOcupadas)}
            required
            className="rounded border p-2"
            value={vagas}
            onChange={(e) => setVagas(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Status
          <select
            className="rounded border p-2"
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            <option value="aberto">Aberto</option>
            <option value="fechado">Fechado</option>
            <option value="cancelado">Cancelado</option>
          </select>
        </label>

        <button
          type="submit"
          className="rounded bg-blue-600 p-2 font-medium text-white"
        >
          Salvar alterações
        </button>
      </form>

      {erro && <p className="mt-4 text-red-600">{erro}</p>}
    </main>
  );
}
