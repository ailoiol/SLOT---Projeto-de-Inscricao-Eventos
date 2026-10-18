import type { Evento } from "../../types";
import { notFound } from "next/navigation";
import BotaoInscrever from "../../components/BotaoInscrever";

export default async function EventoPage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const { id } = await params;

  const resposta = await fetch(
    `${process.env.NEXT_PUBLIC_API_URL}/eventos/${id}`,
    {
      cache: "no-store",
    },
  );
  if (!resposta.ok) {
    notFound();
  }
  const evento: Evento = await resposta.json();

  return (
    <main className="p-8">
      <h1 className="text-3xl font-bold">{evento.titulo}</h1>
      <p className="mt-2 text-gray-600 dark:text-gray-400">
        {evento.descricao}
      </p>
      <p className="mt-4">
        Data:{" "}
        {new Date(evento.dataEvento).toLocaleString("pt-BR", {
          dateStyle: "short",
          timeStyle: "short",
        })}
      </p>
      <p>
        Vagas: {evento.vagasOcupadas}/{evento.vagasTotais}
      </p>
      <p>Status: {evento.status}</p>
      <p>Organizador: {evento.organizador.nome}</p>
      <BotaoInscrever
        eventoId={evento.id}
        aberto={evento.status === "aberto"}
      />
    </main>
  );
}
