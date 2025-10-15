package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.ClientCreateDto;
import imusic.backend.dto.update.ops.ClientUpdateDto;
import imusic.backend.dto.request.ops.ClientRequestDto;
import imusic.backend.dto.response.ops.ClientResponseDto;

import java.util.List;

public interface ClientService {
    List<ClientResponseDto> getAllClients(ClientRequestDto request);
    ClientResponseDto getClientById(Long id);
    ClientResponseDto getClientProfileByUserId(Long userId);
    ClientResponseDto createClient(ClientCreateDto dto);
    ClientResponseDto updateClient(Long id, ClientUpdateDto dto);
    void deleteClient(Long id);
    List<ClientResponseDto> getClientsWithFilters(ClientRequestDto request);
}
