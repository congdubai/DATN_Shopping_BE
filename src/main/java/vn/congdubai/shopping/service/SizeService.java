package vn.congdubai.shopping.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.congdubai.shopping.domain.Size;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.SizeRepository;

@Service
public class SizeService {

    private final SizeRepository sizeRepository;

    public SizeService(
            SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }

    public Specification<Size> notDeletedSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false);
    }

    // Fetch size by id
    public Size handleFetchSizeById(long id) {
        Optional<Size> sizeOptional = this.sizeRepository.findById(id);
        if (sizeOptional.isPresent())
            return sizeOptional.get();
        return null;
    }

    // Fetch all size
    public ResultPaginationDTO handleFetchSizes(Specification<Size> spec, Pageable pageable) {

        Specification<Size> notDeletedSpec = notDeletedSpec().and(spec);
        Page<Size> pSize = this.sizeRepository.findAll(notDeletedSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pSize.getTotalPages());
        mt.setTotal(pSize.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pSize.getContent());
        return rs;
    }

    // Check exist By Name
    public boolean existByName(String name) {
        return this.sizeRepository.existsByName(name);
    }

    // Create new size
    public Size handleCreateSize(Size size) {
        return this.sizeRepository.save(size);
    }

    // Update size
    public Size handleUpdateSize(Size size) {
        Size currentSize = this.handleFetchSizeById(size.getId());
        if (currentSize != null) {
            currentSize.setName(size.getName());
            currentSize.setDescription(size.getDescription());
            this.sizeRepository.save(currentSize);
        }
        return currentSize;
    }

    // Delete size
    public void handleDeleteSize(long id) {
        this.sizeRepository.softDeleteSize(id);
    }
}
