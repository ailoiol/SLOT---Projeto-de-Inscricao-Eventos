"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function CadastroPage() {
  const router = useRouter();
  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");
  const [telefone, setTelefone] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [tipo, setTipo] = useState("participante");
  const [erro, setErro] = useState("");

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro("");

    const resposta = await fetch(
      `${process.env.NEXT_PUBLIC_API_URL}/usuarios`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          nome,
          cpf,
          telefone,
          email,
          senhaHash: senha,
          tipo,
        }),
      },
    );

    if (!resposta.ok) {
      setErro(
        "Não foi possível criar a conta. Confira os dados e tente de novo.",
      );
      return;
    }

    router.push("/login");
  }

  return (
    <main className="mx-auto max-w-sm p-8">
      <h1 className="text-3xl font-bold">Criar conta</h1>

      <form onSubmit={handleSubmit} className="mt-6 flex flex-col gap-4">
        <label className="flex flex-col gap-1">
          Nome
          <input
            type="text"
            className="rounded border p-2"
            value={nome}
            onChange={(e) => setNome(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          CPF
          <input
            type="text"
            className="rounded border p-2"
            value={cpf}
            onChange={(e) => setCpf(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Telefone
          <input
            type="tel"
            className="rounded border p-2"
            value={telefone}
            onChange={(e) => setTelefone(e.target.value)}
          />
        </label>

        <label className="flex flex-col gap-1">
          Email
          <input
            type="email"
            className="rounded border p-2"
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

        <label className="flex flex-col gap-1">
          Tipo de conta
          <select
            className="rounded border p-2"
            value={tipo}
            onChange={(e) => setTipo(e.target.value)}
          >
            <option value="participante">Participante</option>
            <option value="organizador">Organizador</option>
          </select>
        </label>

        <button
          type="submit"
          className="rounded bg-blue-600 p-2 font-medium text-white"
        >
          Criar conta
        </button>
      </form>

      {erro && <p className="mt-4 text-red-600">{erro}</p>}
    </main>
  );
}
