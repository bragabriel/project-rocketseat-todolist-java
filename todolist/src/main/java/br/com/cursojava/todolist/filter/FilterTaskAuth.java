package br.com.cursojava.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.cursojava.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Toda classe que queremos que o Spring vai gerenciar, precisa do Component (classe mais genérica de gerenciamento do Spring)
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override // Toda requisição, antes de passar pela Controller, passa pelo Filter
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Verificando se é a rota de Tasks
        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {

            // Pegar a autenticação (usuário e senha)
            var authorization = request.getHeader("Authorization"); // result: "Basic YnJhZ2FicmllbDoxMjM0NQ==" -> base64 
            
            // removendo a palavra "Basic" + removendo espaços em branco
            var authEncoded = authorization.substring("Basic".length()).trim(); // result: "YnJhZ2FicmllbDoxMjM0NQ=="

            // convertendo para array de bytes
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded); // result: "[B@3136de1"

            var authString = new String(authDecoded); // result: "bragabriel:12345"

            String[] credentials = authString.split(":"); // result: ["bragabriel", "12345"]
            String username = credentials[0];
            String password = credentials[1];

            // Validar Usuário
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                response.sendError(401);
            } else {
                // Validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                // retorna true se a senha estiver correta
                if (passwordVerify.verified) {
                    //Segue viagem

                    //Passando na request para o controller o idUser
                    request.setAttribute("idUser", user.getId());

                    filterChain.doFilter(request, response); // se chegou até aqui, queremos que 'siga viagem'
                } else {
                    response.sendError(401);
                }
            }
        }else{
            filterChain.doFilter(request, response); // se chegou até aqui, queremos que 'siga viagem'
        }
    }
}
