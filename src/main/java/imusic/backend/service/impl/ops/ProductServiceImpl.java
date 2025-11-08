package imusic.backend.service.impl.ops;

import imusic.backend.dto.create.ops.ProductAttributeCreateDto;
import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.dto.update.ops.ProductAttributeUpdateDto;
import imusic.backend.dto.update.ops.ProductUpdateDto;
import imusic.backend.dto.request.ops.ProductRequestDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.entity.ops.Product;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.ProductMapper;
import imusic.backend.mapper.resolver.ref.ProductCategoryResolver;
import imusic.backend.mapper.resolver.ref.ProductUnitResolver;
import imusic.backend.repository.ops.InventoryMovementRepository;
import imusic.backend.repository.ops.OrderItemRepository;
import imusic.backend.repository.ops.ProductRepository;
import imusic.backend.service.ops.CategoryAttributeService;
import imusic.backend.service.ops.ProductAttributeService;
import imusic.backend.service.ops.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductCategoryResolver categoryResolver;
    private final ProductUnitResolver unitResolver;
    private final ProductAttributeService productAttributeService;
    private final CategoryAttributeService categoryAttributeService;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Cacheable(cacheNames = "products", key = "'all'")
    public List<ProductResponseDto> getAll() {
       return productRepository.findAll().stream()
               .map(productMapper::toResponse)
               .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "products", key = "#id")
    public ProductResponseDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Товар не найден, ID: " + id));
        return productMapper.toResponse(product);
    }

    @Override
    @CachePut(cacheNames = "products", key = "#result.id")
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponseDto create(ProductCreateDto dto) {
        Product product = productMapper.toEntity(dto, categoryResolver, unitResolver);
        product.setCreatedAt(LocalDateTime.now());

        if (dto.getWarehouseQuantity() == null || dto.getWarehouseQuantity() < 0) {
            throw new AppException("Количество на складе должно быть задано и неотрицательно");
        }
        product.setWarehouseQuantity(dto.getWarehouseQuantity());
        product.setStockQuantity(dto.getWarehouseQuantity());

        product = productRepository.save(product);

        List<CategoryAttributeResponseDto> categoryAttributes =
                categoryAttributeService.getByCategoryId(product.getCategory().getId());

        Map<Long, String> incomingAttrValues = dto.getAttributes() != null
                ? dto.getAttributes().stream()
                .collect(Collectors.toMap(ProductAttributeResponseDto::getCategoryAttributeId,
                        ProductAttributeResponseDto::getValue))
                : Map.of();

        for (CategoryAttributeResponseDto catAttr : categoryAttributes) {
            ProductAttributeCreateDto productAttrDto = new ProductAttributeCreateDto();
            productAttrDto.setProductId(product.getId());
            productAttrDto.setCategoryAttributeId(catAttr.getId());
            productAttrDto.setValue(incomingAttrValues.getOrDefault(
                    catAttr.getId(), catAttr.getDefaultValue() != null ? catAttr.getDefaultValue() : ""));
            productAttributeService.create(productAttrDto);
        }

        ProductResponseDto response = productMapper.toResponse(product);
        response.setAttributes(productAttributeService.getByProductId(product.getId()));
        return response;
    }

    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductResponseDto update(Long id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Товар не найден, ID: " + id));

        product.setUpdatedAt(LocalDateTime.now());
        productMapper.updateEntity(dto, categoryResolver, unitResolver, product);
        product = productRepository.save(product);

        if (dto.getAttributes() != null) {
            for (ProductAttributeResponseDto attrDto : dto.getAttributes()) {
                ProductAttributeUpdateDto updateDto = new ProductAttributeUpdateDto();
                updateDto.setProductId(product.getId());
                updateDto.setCategoryAttributeId(attrDto.getCategoryAttributeId());
                updateDto.setValue(attrDto.getValue());
                productAttributeService.updateOrCreate(updateDto);
            }
        }

        ProductResponseDto response = productMapper.toResponse(product);
        response.setAttributes(productAttributeService.getByProductId(product.getId()));
        return response;
    }


    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Товар не найден, ID: " + id));
        inventoryMovementRepository.deleteByProductId(id);
        orderItemRepository.deleteByProductId(id);
        productRepository.delete(product);
    }

    @Override
    @CacheEvict(cacheNames = "products" , allEntries = true)
    public void uploadProductImage(Long id, MultipartFile file) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Товар не найден, ключ: " + id));

        if (file == null || file.isEmpty()) {
            throw new AppException("Файл изображения не может быть пустым");
        }

        try {
            String imageName = "product_" + id + "_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path imagePath = Paths.get("D:/JProjects/IMusic/backend/src/main/resources/uploads/products", imageName);
            Files.createDirectories(imagePath.getParent());
            file.transferTo(imagePath);
            product.setImagePath("/products/" + imageName);
            productRepository.save(product);
        } catch (Exception e) {
            throw new AppException("Не удалось загрузить изображение товара: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(cacheNames = "productAttributes", key = "#productId")
    public List<ProductAttributeResponseDto> getAttributesByProductId(Long productId) {
        return productAttributeService.getAll().stream()
                .filter(attr -> attr.getProductId() != null && attr.getProductId().equals(productId))
                .toList();
    }

    @Override
    public List<CategoryAttributeResponseDto> getCategoryAttributesWithValues(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Продукт не найден с id=" + productId));

        List<CategoryAttributeResponseDto> categoryAttributes = categoryAttributeService.getByCategoryId(product.getCategory().getId());
        List<ProductAttributeResponseDto> productAttributes = productAttributeService.getAllByProductId(productId);

        Map<Long, String> attrValuesMap = productAttributes.stream()
                .collect(Collectors.toMap(ProductAttributeResponseDto::getCategoryAttributeId,
                        ProductAttributeResponseDto::getValue));

        categoryAttributes.forEach(catAttrDto -> {
            String value = attrValuesMap.getOrDefault(catAttrDto.getId(),
                    catAttrDto.getDefaultValue() != null ? catAttrDto.getDefaultValue() : "");
            catAttrDto.setValue(value);
        });

        return categoryAttributes;
    }

    @Override
    public PageResponseDto<ProductResponseDto> getPagedProducts(ProductRequestDto request) {
        List<Product> products = productRepository.findAll();

        if (request.getFilters() != null && !request.getFilters().isEmpty()) {
            for (String filter : request.getFilters()) {
                if (filter.startsWith("name:")) {
                    String value = filter.substring(5).trim().toLowerCase();
                    products = products.stream()
                            .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(value))
                            .collect(Collectors.toList());
                }
            }
        }

        if (request.getCategoryId() != null) {
            products = products.stream()
                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(request.getCategoryId()))
                    .collect(Collectors.toList());
        }

        if (request.getMinPrice() != null)
            products = products.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice() >= request.getMinPrice())
                    .collect(Collectors.toList());

        if (request.getMaxPrice() != null)
            products = products.stream()
                    .filter(p -> p.getPrice() != null && p.getPrice() <= request.getMaxPrice())
                    .collect(Collectors.toList());

        if (request.getMinStockLevel() != null)
            products = products.stream()
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() >= request.getMinStockLevel())
                    .collect(Collectors.toList());

        if (request.getMaxStockLevel() != null)
            products = products.stream()
                    .filter(p -> p.getStockQuantity() != null && p.getStockQuantity() <= request.getMaxStockLevel())
                    .collect(Collectors.toList());

        if (request.getMinWarehouseQuantity() != null)
            products = products.stream()
                    .filter(p -> p.getWarehouseQuantity() != null && p.getWarehouseQuantity() >= request.getMinWarehouseQuantity())
                    .collect(Collectors.toList());

        if (request.getMaxStockLevel() != null)
            products = products.stream()
                    .filter(p -> p.getWarehouseQuantity() != null && p.getWarehouseQuantity() <= request.getMaxWarehouseQuantity())
                    .collect(Collectors.toList());
        products.sort(getProductSortComparator(request.getSortBy(), request.getSortDirection()));

        int totalElements = products.size();
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), totalElements);

        List<ProductResponseDto> content = products.subList(fromIndex, toIndex)
                .stream()
                .map(productMapper::toResponse)
                .toList();

        return new PageResponseDto<>(content, request.getPage(), request.getSize(), totalElements, totalPages);
    }



    private Comparator<Product> getProductSortComparator(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }
        Comparator<Product> comparator = switch (sortBy) {
            case "name" -> Comparator.comparing(
                    Product::getName, Comparator.nullsLast(String::compareToIgnoreCase));
            case "price" -> Comparator.comparing(
                    Product::getPrice, Comparator.nullsLast(Float::compareTo));
            case "stockQuantity" -> Comparator.comparing(
                    Product::getStockQuantity, Comparator.nullsLast(Integer::compareTo));
            case "warehouseQuantity" -> Comparator.comparing(
                    Product::getWarehouseQuantity, Comparator.nullsLast(Integer::compareTo));
            case "createdAt" -> Comparator.comparing(
                    Product::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
            default -> Comparator.comparing(
                    Product::getId, Comparator.nullsLast(Long::compareTo));
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }
        return comparator;
    }

}
