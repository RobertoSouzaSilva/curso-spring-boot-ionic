package com.robertosouza.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.robertosouza.cursomc.domain.Pedido;

public interface EmailService {
	
	void senderOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);

}
