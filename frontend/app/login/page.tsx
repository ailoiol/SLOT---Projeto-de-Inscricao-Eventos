"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import type { Usuario } from "../types";
import Link from "next/link";

export default function LoginPage() {
  const router = useRouter();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState("");

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro("");

    const resposta = await fetch(`${process.env.NEXT_PUBLIC_API_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, senha }),
    });

    if (!resposta.ok) {
      const corpo = await resposta.json();
      setErro(corpo.erro ?? "Email ou senha inválidos");
      return;
    }

    const dados: Usuario = await resposta.json();
    localStorage.setItem("usuario", JSON.stringify(dados));
    router.push("/eventos");
  }

  return (
    <main className="mx-auto max-w-sm p-8">
      <h1 className="text-3xl font-bold">Entrar</h1>

      <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4">
        <label className="flex flex-col gap-1">
          Email
          <input
            type="email"
            className="rounded border p-2"
            placeholder="voce@exemplo.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Senha
          <input
            type="password"
            className="rounded border p-2"
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
          />
        </label>

        <button
          type="submit"
          className="rounded bg-blue-600 p-2 font-medium text-white"
        >
          Entrar
        </button>
      </form>

      {erro && <p className="mt-4 text-red-600">{erro}</p>}
      <p className="mt-4 text-sm">
        Não tem conta?{" "}
        <Link href="/cadastro" className="underline">
          Criar conta
        </Link>
      </p>
    </main>
  );
}
