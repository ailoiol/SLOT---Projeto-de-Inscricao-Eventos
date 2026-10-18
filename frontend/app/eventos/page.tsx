import type { Evento } from "../types";
import Link from "next/link";

export default async function EventosPage() {
  const resposta = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/eventos`, {
    cache: "no-store",
  });
  const eventos: Evento[] = await resposta.json();

  return (
    <main className="p-8">
      <h1 className="text-3xl font-bold">Mural de eventos</h1>
      <ul className="grid gap-4 mt-6 sm:grid-cols-2 lg:grid-cols-3">
        {eventos.map((evento) => (
          <li className="rounded-lg border p-4 shadow" key={evento.id}>
            <h2 className="text-xl font-bold">
              <Link href={`/eventos/${evento.id}`}>{evento.titulo}</Link>
            </h2>
            <p className="text-sm text-gray-600 dark:text-gray-400">
              {evento.descricao}
            </p>
            <p>
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
          </li>
        ))}
      </ul>
    </main>
  );
}
