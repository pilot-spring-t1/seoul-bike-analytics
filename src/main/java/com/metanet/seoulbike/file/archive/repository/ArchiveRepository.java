package com.metanet.seoulbike.file.archive.repository;

import com.metanet.seoulbike.file.archive.dto.ArchiveDto;
import com.metanet.seoulbike.file.archive.dto.ArchiveSearchDto;
import com.metanet.seoulbike.file.archive.mapper.ArchiveMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ArchiveRepository implements IArchiveRepository {

    private final ArchiveMapper archiveMapper;

    @Override
    public void save(ArchiveDto dto) {
        archiveMapper.insertArchive(dto);
    }

    @Override
    public int countAll(ArchiveSearchDto searchDto) {
        return archiveMapper.selectArchiveCount(searchDto);
    }

    @Override
    public List<ArchiveDto> findAll(ArchiveSearchDto searchDto) {
        return archiveMapper.selectArchiveList(searchDto);
    }

    @Override
    public ArchiveDto findById(Long archiveId) {
        return archiveMapper.selectArchiveById(archiveId);
    }

    @Override
    public void delete(Long archiveId) {
        archiveMapper.deleteArchive(archiveId);
    }
}