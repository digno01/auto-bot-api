//package br.gov.mme.auth.controller;
//
//import br.gov.mme.auth.exceptions.RegistroDuplicadoException;
//import br.gov.mme.auth.mock.UserMock;
//import br.gov.mme.auth.repository.UserRepository;
//import br.gov.mme.auth.service.UserService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.modelmapper.ModelMapper;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class UserControllerTest {
//    @InjectMocks
//    private UserController controller;
//    @Mock
//    private UserService service;
//
//    @Mock
//    private UserRepository repository;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @BeforeEach
//    void init(){
//        modelMapper = new ModelMapper();
//    }
//
//    @Test
//    void createTest() throws RegistroDuplicadoException {
//        Mockito.doNothing().when(service).validateFields(UserMock.getUserDTO());
//        controller.create(UserMock.getUserDTO());
//    }
//
//}
