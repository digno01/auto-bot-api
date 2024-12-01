package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.MessageDTO;
import br.com.auto.bot.auth.dto.filtro.FiltroLoginDTO;
import br.com.auto.bot.auth.exceptions.RegistroNaoEncontradoException;
import br.com.auto.bot.auth.generic.GenericController;
import br.com.auto.bot.auth.dto.UserDTO;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.service.UserService;
import br.com.auto.bot.auth.util.ObterDadosUsuarioLogado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("${app.api.url}/usuario")
@Tag(name = "Usuários", description = "Este controlador fornece operações para gerenciar usuários e autenticação.")
//@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class UserController extends GenericController<User, UserDTO, Long> {

    private final UserService serviceBean;

    public UserController(UserService service) {
        super(service);
        this.serviceBean = service;
    }

    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista paginada de usuários.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuários não encontrados.")
    })
    @GetMapping("/listar")
    @Override
    public ResponseEntity<Page<UserDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> entities = this.serviceBean.findAllByIsDeletedFalseAndPerfilAcesso(pageable);
        Page<UserDTO> tos = entities.map(this::toTO);
        return ResponseEntity.ok(tos);
    }

    @Operation(summary = "Filtrar usuários", description = "Retorna uma lista paginada de usuários filtrados com base nos critérios fornecidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuários filtrada retornada com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuários não encontrados.")
    })
    @PostMapping("/filtro")
    public ResponseEntity<Page<UserDTO>> getAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            HttpServletRequest httpServletRequest,
            @Valid @RequestBody FiltroLoginDTO filtro) throws RegistroNaoEncontradoException {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        filtro.setSitema(httpServletRequest.getHeader("sistema"));
        Page<User> entities = this.serviceBean.findAllByFiltro(pageable, filtro);
        Page<UserDTO> tos = entities.map(this::toTO);
        return ResponseEntity.ok(tos);
    }

    @Operation(summary = "Obter usuário por ID", description = "Retorna os detalhes de um usuário específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @GetMapping
    public ResponseEntity<UserDTO> getById(HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        User user = this.serviceBean.findById(ObterDadosUsuarioLogado.getUsuarioLogadoId(), httpServletRequest.getHeader("sistema"));
        user.setPassword(null); // Não retornar a senha
        return ResponseEntity.ok(this.toTO(user));
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza as informações de um usuário existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado.")
    })
    @PutMapping("")
    public ResponseEntity  update(@Valid @RequestBody UserDTO to, HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        to.setSistema(httpServletRequest.getHeader("sistema"));
        to.setId(ObterDadosUsuarioLogado.getUsuarioLogadoId());
        User savedEntity = serviceBean.update(to);
        return ResponseEntity.ok(new MessageDTO("Atualizado com sucesso."));
    }

    @Operation(summary = "Obter informações do usuário autenticado", description = "Retorna os detalhes do usuário atualmente autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informações do usuário autenticado retornadas com sucesso.")
    })
    @GetMapping("usuario/info")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @Override
    protected User toEntity(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    @Override
    protected UserDTO toTO(User ent) {
        UserDTO user = modelMapper.map(ent, UserDTO.class);
        user.setPassword(null); // Não retornar a senha
        user.setContato(user.getContato().stream().filter(e -> e.getIsDeleted().equals(false)).collect(Collectors.toList()));
        return user;
    }
}
