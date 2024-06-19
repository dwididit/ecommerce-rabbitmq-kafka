package dev.dwidi.ecommercerabbitmqkafka.service;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import dev.dwidi.ecommercerabbitmqkafka.enums.UserRole;
import dev.dwidi.ecommercerabbitmqkafka.exception.EmailAlreadyExistsException;
import dev.dwidi.ecommercerabbitmqkafka.exception.UserNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.exception.UsernameAlreadyExistsException;
import dev.dwidi.ecommercerabbitmqkafka.repository.UserRepository;
import dev.dwidi.ecommercerabbitmqkafka.service.rabbitmq.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private Supplier<String> uuidGenerator;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PublicResponseDTO<UserResponseDTO> createUser(UserRequestDTO userRequestDTO) {
        // Check if username or email already exists
        if (userRepository.findByUsername(userRequestDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String requestId = uuidGenerator.get();

        // Mapping the DTO
        User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword())); // Hash the password
        user.setEmail(userRequestDTO.getEmail());
        user.setAddress(userRequestDTO.getAddress());
        user.setPhone(userRequestDTO.getPhone());
        user.setUserRole(UserRole.ROLE_USER);
        user.setOperation("CREATE");

        // Save the user to the repository
        User savedUser = userRepository.save(user);

        // Reload the user to get the createdAt and updatedAt fields populated
        savedUser = userRepository.findById(savedUser.getId()).orElseThrow(() -> new UserNotFoundException("User not found after saving"));

        // Send the user entity to RabbitMQ
        rabbitMQSender.send(savedUser);

        // Build the response using the saved user entity
        UserResponseDTO userResponseDTO = new UserResponseDTO(savedUser);

        return new PublicResponseDTO<>(requestId, 201, "User created successfully", userResponseDTO);
    }

    public PublicResponseDTO<UserResponseDTO> updateUser(Long userId, UserRequestDTO userRequestDTO) {
        String requestId = uuidGenerator.get();

        User userToEdit = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Check if the new username already exists and belongs to another user
        if (userRequestDTO.getUsername() != null) {
            userRepository.findByUsername(userRequestDTO.getUsername())
                    .filter(user -> !user.getId().equals(userId))
                    .ifPresent(user -> {
                        throw new UsernameAlreadyExistsException("Please choose other username or leave unchanged");
                    });
        }

        // Check if the new email already exists and belongs to another user
        if (userRequestDTO.getEmail() != null) {
            userRepository.findByEmail(userRequestDTO.getEmail())
                    .filter(user -> !user.getId().equals(userId))
                    .ifPresent(user -> {
                        throw new EmailAlreadyExistsException("Please choose other email or leave unchanged");
                    });
        }
        // Update only the fields that are not null in the UserRequestDTO
        if (userRequestDTO.getUsername() != null) {
            userToEdit.setUsername(userRequestDTO.getUsername());
        }
        if (userRequestDTO.getPassword() != null) {
            // Encrypt the password before setting it
            String encryptedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
            userToEdit.setPassword(encryptedPassword);
        }
        if (userRequestDTO.getEmail() != null) {
            userToEdit.setEmail(userRequestDTO.getEmail());
        }
        if (userRequestDTO.getAddress() != null) {
            userToEdit.setAddress(userRequestDTO.getAddress());
        }
        if (userRequestDTO.getPhone() != null) {
            userToEdit.setPhone(userRequestDTO.getPhone());
        }
        userToEdit.setOperation("UPDATE");

        userRepository.save(userToEdit);
        rabbitMQSender.send(userToEdit);

        // Build the response using the modified constructor
        UserResponseDTO userResponseDTO = new UserResponseDTO(userToEdit);

        return new PublicResponseDTO<>(requestId, 200, "User updated successfully", userResponseDTO);
    }


    public PublicResponseDTO<UserResponseDTO> deleteUser(Long userId) {
        String requestId = uuidGenerator.get();

        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.delete(userToDelete);

        // Create a new User object to send the delete operation to RabbitMQ
        User userToSend = new User();
        userToSend.setId(userId);
        userToSend.setOperation("DELETE");
        rabbitMQSender.send(userToSend);

        return new PublicResponseDTO<>(requestId, 200, "User deleted successfully", null);
    }

    public PublicResponseDTO<UserResponseDTO> getUserById(Long usertId) {
        String requestId = uuidGenerator.get();

        User userToDelete = userRepository.findById(usertId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserResponseDTO userResponseDTO = getUserResponseDTO(userToDelete);

        return new PublicResponseDTO<>(requestId, 200, "User retrieved sucessfully", userResponseDTO);
    }

    public PublicResponseDTO<List<UserResponseDTO>> getAllUsers(Integer page, Integer size) {
        String requestId = uuidGenerator.get();
        List<User> users;
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<User> userPage = userRepository.findAll(pageable);
            users = userPage.getContent();
        } else {
            users = userRepository.findAll();
        }

        List<UserResponseDTO> userResponseDTOs = users.stream().map(user -> {
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            userResponseDTO.setId(user.getId());
            userResponseDTO.setUsername(user.getUsername());
            userResponseDTO.setEmail(user.getEmail());
            userResponseDTO.setAddress(user.getAddress());
            userResponseDTO.setPhone(user.getPhone());
            userResponseDTO.setUserRole(user.getUserRole());
            userResponseDTO.setUpdatedAt(user.getUpdatedAt());
            userResponseDTO.setCreatedAt(user.getCreatedAt());
            return userResponseDTO;
        }).toList();

        PublicResponseDTO<List<UserResponseDTO>> responseDTO = new PublicResponseDTO<>();
        responseDTO.setCode(200);
        responseDTO.setMessage("User retrieved successfully");
        responseDTO.setData(userResponseDTOs);
        responseDTO.setRequestId(requestId);

        return responseDTO;
    }


    // Helper method to build the UserResponseDTO
    private UserResponseDTO getUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setAddress(user.getAddress());
        userResponseDTO.setPhone(user.getPhone());
        userResponseDTO.setCreatedAt(user.getCreatedAt());
        userResponseDTO.setUpdatedAt(user.getUpdatedAt());
        return userResponseDTO;
    }
}