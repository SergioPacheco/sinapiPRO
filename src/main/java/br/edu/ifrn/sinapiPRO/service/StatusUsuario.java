package br.edu.ifrn.sinapiPRO.service;

import br.edu.ifrn.sinapiPRO.repository.UsuariosRepository;

public enum StatusUsuario {

	ATIVAR {
		@Override
		public void executar(Long[] codigos, UsuariosRepository usuariosRepository) {
			usuariosRepository.findByCodigoIn(codigos).forEach(u -> u.setAtivo(true));
		}
	},
	
	DESATIVAR {
		@Override
		public void executar(Long[] codigos, UsuariosRepository usuariosRepository) {
			usuariosRepository.findByCodigoIn(codigos).forEach(u -> u.setAtivo(false));
		}
	};
	
	public abstract void executar(Long[] codigos, UsuariosRepository usuariosRepository);
	
}
