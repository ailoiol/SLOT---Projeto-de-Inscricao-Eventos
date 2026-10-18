export type Organizador = {
  id: number;
  nome: string;
  email: string;
  tipo: string;
  perfilCompleto: boolean;
  criadoEm: string;
};

export type Evento = {
  id: number;
  titulo: string;
  descricao: string | null;
  dataEvento: string;
  vagasTotais: number;
  vagasOcupadas: number;
  status: string;
  organizador: Organizador;
};

export type Usuario = Organizador;

export type Criterio = {
  id: number;
  eventoId: number;
  campo: string;
  obrigatorio: boolean;
};

export type Inscricao = {
  id: number;
  status: string;
  criadoEm: string;
  usuario: Usuario;
  evento: Evento;
  nomeParticipante: string;
  cpfParticipante: string;
  telefoneParticipante: string | null;
};

export type RespostaCriterio = {
  id: number;
  inscricaoId: number;
  criterioPersonalizadoId: number;
  criterioCampo: string;
  valor: string;
};
