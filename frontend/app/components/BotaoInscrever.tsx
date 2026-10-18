"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import type { Usuario } from "../types";

export default function BotaoInscrever({
  eventoId,
  aberto,
}: {
  eventoId: number;
  aberto: boolean;
}) {
  const router = useRouter();
  const [aviso, setAviso] = useState("");

  function handleClick() {
    const salvo = localStorage.getItem("usuario");

    if (!salvo) {
      router.push("/login");
      return;
    }

    const usuario: Usuario = JSON.parse(salvo);

    if (usuario.tipo === "organizador") {
      setAviso("Contas de organizador não podem se inscrever em eventos.");
      return;
    }

    router.push(`/eventos/${eventoId}/inscricao`);
  }

  return (
    <div className="mt-6">
      <button
        onClick={handleClick}
        disabled={!aberto}
        className="rounded bg-blue-600 px-4 py-2 font-medium text-white disabled:cursor-not-allowed disabled:opacity-50"
      >
        {aberto ? "Inscrever-se" : "Inscrições encerradas"}
      </button>
      {aviso && <p className="mt-2 text-red-600">{aviso}</p>}
    </div>
  );
}
