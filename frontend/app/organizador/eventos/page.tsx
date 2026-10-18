"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import type { Evento, Usuario } from "../../types";

export default function EventosDoOrganizadorPage() {
  const router = useRouter();
  const [eventos, setEventos] = useState<Evento[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    async function carregar() {
      const salvo = localStorage.getItem("usuario");
      if (!salvo) {
        router.push("/login");
        return;
      }
      const usuario: Usuario = JSON.parse(salvo);

      if (usuario.tipo !== "organizador") {
        router.push("/eventos");
        return;
      }

      const resposta = await fetch(
        `${process.env.NEXT_PUBLIC_API_URL}/eventos?organizadorId=${usuario.id}`,
      );
      if (resposta.ok) {
        setEventos(await resposta.json());
      }
      setCarregando(false);
    }
    carregar();
  }, [router]);

  if (carregando) {
    return (
      <main className="p-8">
        <p>Carregando...</p>
      </main>
    );
  }

  return (
    <main className="p-8">
      <h1 className="text-3xl font-bold">Meus eventos</h1>

      <Link
        href="/organizador/eventos/novo"
        className="mt-4 inline-block rounded bg-blue-600 px-4 py-2 font-medium text-white"
      >
        Criar evento
      </Link>

      {eventos.length === 0 ? (
        <p className="mt-6">Você ainda não criou nenhum evento.</p>
      ) : (
        <ul className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {eventos.map((evento) => (
            <li key={evento.id} className="rounded-lg border p-4 shadow">
              <h2 className="text-xl font-bold">{evento.titulo}</h2>
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
              <div className="mt-3 flex gap-4">
                <Link
                  href={`/organizador/eventos/${evento.id}/editar`}
                  className="underline"
                >
                  Editar
                </Link>
                <Link
                  href={`/organizador/eventos/${evento.id}/inscricoes`}
                  className="underline"
                >
                  Ver inscritos
                </Link>
              </div>
            </li>
          ))}
        </ul>
      )}
    </main>
  );
}
