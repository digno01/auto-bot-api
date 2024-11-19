package br.com.auto.bot.auth.controller;

import br.com.auto.bot.auth.dto.MessageDTO;
import br.com.auto.bot.auth.dto.filtro.FiltroLoginDTO;
import br.com.auto.bot.auth.exceptions.RegistroNaoEncontradoException;
import br.com.auto.bot.auth.generic.GenericController;
import br.com.auto.bot.auth.dto.UserDTO;
import br.com.auto.bot.auth.model.User;
import br.com.auto.bot.auth.service.UserService;
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
@Tag(name = "Controler Usuario", description = "Este controlador fornece operações de exemplo de CRUD para usuarios e autenticação.")
public class UserController extends GenericController<User, UserDTO, Long> {

    private final UserService serviceBean;

    public UserController(UserService service) {
        super(service);
        this.serviceBean = service;
    }


    @GetMapping
    @Override
    public ResponseEntity<Page<UserDTO>> getAll(@RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                @RequestParam(defaultValue = "id") String sortBy,
                                                @RequestParam(defaultValue = "asc") String sortDirection,
                                                HttpServletRequest httpServletRequest
    ) throws RegistroNaoEncontradoException {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), new String[]{sortBy});
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> entities = this.serviceBean.findAllByIsDeletedFalseAndPerfilAcesso(pageable);
        Page<UserDTO> tos = entities.map(this::toTO);
        return ResponseEntity.ok(tos);
    }

    @PostMapping("/filtro")
    public ResponseEntity<Page<UserDTO>> getAll(@RequestParam(defaultValue = "0") int pageNumber,
                                                @RequestParam(defaultValue = "10") int pageSize,
                                                @RequestParam(defaultValue = "id") String sortBy,
                                                @RequestParam(defaultValue = "asc") String sortDirection,
                                                HttpServletRequest httpServletRequest,
                                                @RequestBody FiltroLoginDTO filtro
    ) throws RegistroNaoEncontradoException {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), new String[]{sortBy});
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        filtro.setSitema(httpServletRequest.getHeader("sistema"));
        Page<User> entities = this.serviceBean.findAllByFiltro(pageable, filtro);
        Page<UserDTO> tos = entities.map(this::toTO);
        return ResponseEntity.ok(tos);
    }

    @GetMapping({"{id}"})
    @Override
    public ResponseEntity<UserDTO> getById(@PathVariable Long id, HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        User user = this.serviceBean.findById(id, httpServletRequest.getHeader("sistema"));
        user.setPassword(null);
        return ResponseEntity.ok(this.toTO(user));
    }

    @PutMapping("{id}")
    @Override
    public ResponseEntity update(@PathVariable Long id, @Valid @RequestBody UserDTO to, HttpServletRequest httpServletRequest) throws RegistroNaoEncontradoException {
        to.setSistema(httpServletRequest.getHeader("sistema"));
        to.setId(id);
        User savedEntity = serviceBean.update(to);
        this.toTO(savedEntity);
        return ResponseEntity.ok(new MessageDTO("Atualizado com sucesso."));
    }


    @Override
    protected User toEntity(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    @Override
    protected UserDTO toTO(User ent) {
        UserDTO user = modelMapper.map(ent, UserDTO.class);
        user.setPassword(null);
        user.setContato(user.getContato().stream().filter(e -> e.getIsDeleted().equals(false)).collect(Collectors.toList()));
        return user;
    }


    @GetMapping("usuario/info")
    public ResponseEntity<User> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok(currentUser);
    }

}
