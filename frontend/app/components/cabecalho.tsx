"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import type { Usuario } from "../types";

export default function Cabecalho() {
  const router = useRouter();
  const pathname = usePathname();
  const [usuario, setUsuario] = useState<Usuario | null>(null);

  useEffect(() => {
    const timeout = window.setTimeout(() => {
      const salvo = localStorage.getItem("usuario");
      setUsuario(salvo ? JSON.parse(salvo) : null);
    }, 0);

    return () => window.clearTimeout(timeout);
  }, [pathname]);

  function sair() {
    localStorage.removeItem("usuario");
    setUsuario(null);
    router.push("/login");
  }

  return (
    <header className="flex items-center justify-between border-b p-4">
      <div className="flex items-center gap-6">
        <Link href="/eventos" className="font-bold">
          Inscrição de Eventos
        </Link>

        {usuario && usuario.tipo !== "organizador" && (
          <Link href="/minhas-inscricoes" className="underline">
            Minhas inscrições
          </Link>
        )}
        {usuario && usuario.tipo === "organizador" && (
          <Link href="/organizador/eventos" className="underline">
            Meus eventos
          </Link>
        )}
      </div>

      {usuario ? (
        <div className="flex items-center gap-4">
          <span>Olá, {usuario.nome}</span>
          <button onClick={sair} className="rounded border px-3 py-1">
            Sair
          </button>
        </div>
      ) : (
        <Link href="/login" className="rounded border px-3 py-1">
          Entrar
        </Link>
      )}
    </header>
  );
}
