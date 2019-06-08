package com.robertosouza.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.robertosouza.cursomc.domain.ItemPedido;
import com.robertosouza.cursomc.domain.PagamentoComBoleto;
import com.robertosouza.cursomc.domain.Pedido;
import com.robertosouza.cursomc.enums.EstadoPagamento;
import com.robertosouza.cursomc.repositories.ItemPedidoRepository;
import com.robertosouza.cursomc.repositories.PagamentoRepository;
import com.robertosouza.cursomc.repositories.PedidoRepository;
import com.robertosouza.cursomc.repositories.ProdutoRepository;
import com.robertosouza.cursomc.resources.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {
	
	@Autowired
	private PedidoRepository repo;
	@Autowired
	private BoletoService boletoService;
	@Autowired
	private PagamentoRepository pagamentoRepository; 
	@Autowired
	private ProdutoService produtoService;
	@Autowired
	private ItemPedidoRepository itemProdutoRepository;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
		"Objeto não encontrado! Id: " + id + ", Tipo: " + Pedido.class.getName()));
	}
	
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip : obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setPreco(produtoService.find(ip.getProduto().getId()).getPreco());
			ip.setPedido(obj);
		}
		itemProdutoRepository.saveAll(obj.getItens());
		return obj;
	}
}
