package vn.congdubai.shopping.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Discount;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.DiscountRepository;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;

    public DiscountService(DiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
    }

    // Fetch all discount
    public ResultPaginationDTO handleFetchDiscounts(Pageable pageable) {
        Page<Discount> pDiscount = this.discountRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pDiscount.getTotalPages());
        mt.setTotal(pDiscount.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pDiscount.getContent());
        return rs;
    }

    // Check exist By Name
    public boolean existByName(String code) {
        return this.discountRepository.existsByCode(code);
    }

    // Create new discount
    public Discount handleCreateDiscount(Discount discount) {
        return this.discountRepository.save(discount);
    }

    // Delete discount
    public void handleDeleteDiscount(long id) {
        this.discountRepository.deleteById(id);
    }

    // Update discount
    public Discount handleUpdateDiscount(Discount discount) {
        Discount currentDiscount = this.handleFetchDiscountById(discount.getId());
        if (currentDiscount != null) {
            currentDiscount.setCode(discount.getCode());
            currentDiscount.setDiscountPercent(discount.getDiscountPercent());
            currentDiscount.setDescription(discount.getDescription());
            currentDiscount.setStartDate(discount.getStartDate());
            currentDiscount.setMaxDiscount(discount.getMaxDiscount());
            currentDiscount.setEndDate(discount.getEndDate());
            currentDiscount.setQuantity(discount.getQuantity());
            this.discountRepository.save(currentDiscount);
        }
        return currentDiscount;
    }

    // Fetch discount by id
    public Discount handleFetchDiscountById(long id) {
        Optional<Discount> discountOptional = this.discountRepository.findById(id);
        if (discountOptional.isPresent())
            return discountOptional.get();
        return null;
    }
}
