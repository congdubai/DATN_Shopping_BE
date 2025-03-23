package vn.congdubai.shopping.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.congdubai.shopping.domain.Color;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.ColorRepository;

@Service
public class ColorService {

    private final ColorRepository colorRepository;

    public ColorService(
            ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public Specification<Color> notDeletedSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false);
    }

    // Fetch color by id
    public Color handleFetchColorById(long id) {
        Optional<Color> colorOptional = this.colorRepository.findById(id);
        if (colorOptional.isPresent())
            return colorOptional.get();
        return null;
    }

    // Fetch all color
    public ResultPaginationDTO handleFetchColors(Specification<Color> spec, Pageable pageable) {

        Specification<Color> notDeletedSpec = notDeletedSpec().and(spec);
        Page<Color> pColor = this.colorRepository.findAll(notDeletedSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pColor.getTotalPages());
        mt.setTotal(pColor.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pColor.getContent());
        return rs;
    }

    // Check exist By Name
    public boolean existByName(String name) {
        return this.colorRepository.existsByName(name);
    }

    // Create new color
    public Color handleCreateColor(Color color) {
        return this.colorRepository.save(color);
    }

    // Update color
    public Color handleUpdateColor(Color color) {
        Color currentColor = this.handleFetchColorById(color.getId());
        if (currentColor != null) {
            currentColor.setName(color.getName());
            currentColor.setDescription(color.getDescription());
            this.colorRepository.save(currentColor);
        }
        return currentColor;
    }

    // Delete color
    // delete a product
    public void handleDeleteColor(long id) {
        this.colorRepository.softDeleteColor(id);
    }
}
