package com.altis.library_backend.publishers.services;

import com.altis.library_backend.publishers.models.dtos.PublisherRequestDTO;
import com.altis.library_backend.publishers.models.dtos.PublisherResponseDTO;
import com.altis.library_backend.publishers.models.dtos.UpdateRequestDTO;
import com.altis.library_backend.publishers.models.dtos.UpdateResponseDTO;
import com.altis.library_backend.publishers.models.entities.Publishers;
import com.altis.library_backend.publishers.repositories.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PublisherService {
    private final PublisherRepository publishersRepository;

    public PublisherService(PublisherRepository publishersRepository) {
        this.publishersRepository = publishersRepository;
    }

    @Transactional(readOnly = true)
    public PublisherResponseDTO findById(Long id){
        Publishers findPublisher = publishersRepository.findPublisherById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with ID: "+id));
    return new PublisherResponseDTO(
            findPublisher.getId(),
            findPublisher.getName(),
            findPublisher.getCnpj(),
            findPublisher.getEmail(),
            findPublisher.getPhone(),
            findPublisher.getAddress()
    );
    }

    @Transactional(readOnly = true)
    public List<PublisherResponseDTO> findAll(){
        List<Publishers> publishers = publishersRepository.findAll();

        List<PublisherResponseDTO> responses = new ArrayList<>();

        for (Publishers publisher : publishers){
            PublisherResponseDTO response = new PublisherResponseDTO(
                    publisher.getId(),
                    publisher.getName(),
                    publisher.getCnpj(),
                    publisher.getEmail(),
                    publisher.getPhone(),
                    publisher.getAddress()
            );
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public PublisherResponseDTO createPublisher(PublisherRequestDTO request){
        if (publishersRepository.existsByCnpj(request.cnpj())){
            throw new IllegalArgumentException("The cnpj is used");
        }
        if (publishersRepository.existsByEmail((request.email()))){
            throw new IllegalArgumentException(("The email is used"));
        }

        Publishers newPublisher = new Publishers();
        newPublisher.setName(request.name());
        newPublisher.setCnpj(request.cnpj());
        newPublisher.setEmail(request.email());
        newPublisher.setPhone(request.phone());
        newPublisher.setAddress(request.address());

        Publishers savedPublishers = publishersRepository.save(newPublisher);

        return new PublisherResponseDTO(
                savedPublishers.getId(),
                savedPublishers.getName(),
                savedPublishers.getCnpj(),
                savedPublishers.getEmail(),
                savedPublishers.getPhone(),
                savedPublishers.getAddress()
        );
    }

    @Transactional
    public UpdateResponseDTO updatePublisher(Long id, UpdateRequestDTO request) {

        Publishers existingPublisher = publishersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Publisher not found"));

        if (request.email() != null
                && !request.email().isBlank()
                && !existingPublisher.getEmail().equals(request.email())
                && publishersRepository.existsByEmail(request.email())) {

            throw new IllegalArgumentException("Email already in use by another publisher");
        }

        if (request.name() != null && !request.name().isBlank()) {
            existingPublisher.setName(request.name());
        }

        if (request.email() != null && !request.email().isBlank()) {
            existingPublisher.setEmail(request.email());
        }

        if (request.phone() != null && !request.phone().isBlank()) {
            existingPublisher.setPhone(request.phone());
        }

        if (request.address() != null && !request.address().isBlank()) {
            existingPublisher.setAddress(request.address());
        }

        Publishers savedPublisher = publishersRepository.save(existingPublisher);

        return new UpdateResponseDTO(
                savedPublisher.getId(),
                savedPublisher.getName(),
                savedPublisher.getEmail(),
                savedPublisher.getPhone(),
                savedPublisher.getAddress()
        );
    }

    @Transactional
    public void deletePublisher(Long id){
        if(!publishersRepository.existsById(id)){
            throw new IllegalArgumentException("Publisher not found with ID: " + id);
        }

        publishersRepository.deleteById(id);
    }

}