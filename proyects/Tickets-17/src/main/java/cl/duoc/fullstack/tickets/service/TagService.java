package cl.duoc.fullstack.tickets.service;

import cl.duoc.fullstack.tickets.dto.TagRequest;
import cl.duoc.fullstack.tickets.model.Tag;
import cl.duoc.fullstack.tickets.respository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TagService {
  private final TagRepository tagRepository;

  public TagService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  public Tag create(TagRequest request) {
    if (tagRepository.existsByNameIgnoreCase(request.name())) {
      throw new IllegalArgumentException("Tag with this name already exists");
    }
    Tag tag = new Tag();
    tag.setName(request.name());
    tag.setColor(request.color());
    return tagRepository.save(tag);
  }

  public List<Tag> findAll() {
    return tagRepository.findAll();
  }

  public Optional<Tag> findById(Long id) {
    return tagRepository.findById(id);
  }

  public Optional<Tag> update(Long id, TagRequest request) {
    return tagRepository.findById(id).map(tag -> {
      if (!tag.getName().equalsIgnoreCase(request.name())
          && tagRepository.existsByNameIgnoreCase(request.name())) {
        throw new IllegalArgumentException("Tag with this name already exists");
      }
      tag.setName(request.name());
      tag.setColor(request.color());
      return tagRepository.save(tag);
    });
  }

  public boolean deleteById(Long id) {
    if (tagRepository.existsById(id)) {
      tagRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
