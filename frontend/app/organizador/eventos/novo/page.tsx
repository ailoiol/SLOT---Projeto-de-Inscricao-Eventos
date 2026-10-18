"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import type { Evento, Usuario } from "../../../types";

type CriterioNovo = {
  campo: string;
  obrigatorio: boolean;
};

export default function NovoEventoPage() {
  const router = useRouter();
  const [usuario, setUsuario] = useState<Usuario | null>(() => {
    if (typeof window === "undefined") return null;

    const salvo = localStorage.getItem("usuario");
    if (!salvo) return null;

    try {
      const logado: Usuario = JSON.parse(salvo);
      return logado.tipo === "organizador" ? logado : null;
    } catch {
      return null;
    }
  });
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [dataEvento, setDataEvento] = useState("");
  const [vagas, setVagas] = useState("");
  const [criterios, setCriterios] = useState<CriterioNovo[]>([]);
  const [erro, setErro] = useState("");

  useEffect(() => {
    if (typeof window === "undefined") return;

    const salvo = localStorage.getItem("usuario");
    if (!salvo) {
      router.push("/login");
      return;
    }

    try {
      const logado: Usuario = JSON.parse(salvo);
      if (logado.tipo !== "organizador") {
        router.push("/eventos");
      }
    } catch {
      router.push("/login");
    }
  }, [router]);

  function adicionarCriterio() {
    setCriterios([...criterios, { campo: "", obrigatorio: false }]);
  }

  function atualizarCriterio(indice: number, mudancas: Partial<CriterioNovo>) {
    setCriterios(
      criterios.map((c, i) => (i === indice ? { ...c, ...mudancas } : c)),
    );
  }

  function removerCriterio(indice: number) {
    setCriterios(criterios.filter((_, i) => i !== indice));
  }

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro("");
    if (!usuario) return;

    const resposta = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/eventos`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        organizador: { id: usuario.id },
        titulo,
        descricao: descricao || null,
        dataEvento,
        vagasTotais: Number(vagas),
      }),
    });

    if (!resposta.ok) {
      const corpo = await resposta.json();
      setErro(corpo.erro ?? "Não foi possível criar o evento.");
      return;
    }

    const evento: Evento = await resposta.json();

    for (const criterio of criterios) {
      if (!criterio.campo.trim()) continue;

      const respostaCriterio = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/criterios-personalizados`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            evento: { id: evento.id },
            campo: criterio.campo,
            obrigatorio: criterio.obrigatorio,
          }),
        },
      );

      if (!respostaCriterio.ok) {
        setErro(
          "O evento foi criado, mas não foi possível salvar uma das perguntas.",
        );
        return;
      }
    }

    router.push("/organizador/eventos");
  }

  if (!usuario) {
    return (
      <main className="p-8">
        <p>Carregando...</p>
      </main>
    );
  }

  return (
    <main className="mx-auto max-w-sm p-8">
      <h1 className="text-3xl font-bold">Criar evento</h1>

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
          Número de vagas
          <input
            type="number"
            min={1}
            required
            className="rounded border p-2"
            value={vagas}
            onChange={(e) => setVagas(e.target.value)}
          />
        </label>

        <fieldset className="flex flex-col gap-3">
          <legend className="font-medium">
            Perguntas para os participantes (opcional)
          </legend>

          {criterios.map((criterio, indice) => (
            <div
              key={indice}
              className="flex flex-col gap-2 rounded border p-3"
            >
              <input
                type="text"
                placeholder="Ex.: Tamanho da camiseta"
                className="rounded border p-2"
                value={criterio.campo}
                onChange={(e) =>
                  atualizarCriterio(indice, { campo: e.target.value })
                }
              />
              <label className="flex items-center gap-2 text-sm">
                <input
                  type="checkbox"
                  checked={criterio.obrigatorio}
                  onChange={(e) =>
                    atualizarCriterio(indice, { obrigatorio: e.target.checked })
                  }
                />
                Resposta obrigatória
              </label>
              <button
                type="button"
                onClick={() => removerCriterio(indice)}
                className="self-start text-sm text-red-600 underline"
              >
                Remover
              </button>
            </div>
          ))}

          <button
            type="button"
            onClick={adicionarCriterio}
            className="self-start rounded border px-3 py-1"
          >
            + Adicionar pergunta
          </button>
        </fieldset>

        <button
          type="submit"
          className="rounded bg-blue-600 p-2 font-medium text-white"
        >
          Criar evento
        </button>
      </form>

      {erro && <p className="mt-4 text-red-600">{erro}</p>}
    </main>
  );
}
