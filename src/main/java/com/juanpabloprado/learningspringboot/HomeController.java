package com.juanpabloprado.learningspringboot;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

  private static final String BASE_PATH = "/images";
  private static final String FILENAME = "{filename:.+}";

  private final ImageService imageService;

  @Autowired
  public HomeController(ImageService imageService) {
    this.imageService = imageService;
  }

  @RequestMapping(value = "/")
  public String index(Model model, Pageable pageable) {
    final Page<Image> page = imageService.findPage(pageable);
    model.addAttribute("page", page);
    return "index";
  }

  @RequestMapping(method = RequestMethod.GET, value = BASE_PATH + "/" + FILENAME + "/raw")
  @ResponseBody
  public ResponseEntity<?> oneRawImage(@PathVariable String filename) {
    try {
      Resource file = imageService.findOneImage(filename);
      return ResponseEntity.ok()
          .contentLength(file.contentLength())
          .contentType(MediaType.IMAGE_JPEG)
          .body(new InputStreamResource(file.getInputStream()));
    } catch (IOException e) {
      return ResponseEntity.badRequest()
          .body("Couldn't find " + filename + " => " + e.getMessage());
    }
  }

  @RequestMapping(method = RequestMethod.POST, value = BASE_PATH)
  public String createFile(@RequestParam("file") MultipartFile file,
      RedirectAttributes redirectAttributes) throws URISyntaxException {
    try {
      imageService.createImage(file);
      redirectAttributes.addFlashAttribute("flash.message", "Successfully uploaded " + file.getOriginalFilename());
    } catch (IOException e) {
      redirectAttributes.addFlashAttribute("flash.message", "Failed to upload " + file.getOriginalFilename() + " => " + e.getMessage());
    }
    return "redirect:/";
  }

  @RequestMapping(method = RequestMethod.DELETE, value = BASE_PATH + "/" + FILENAME)
  public String deleteFile(@PathVariable String filename, RedirectAttributes redirectAttributes) {
    try {
      imageService.deleteImage(filename);
      redirectAttributes.addFlashAttribute("flash.message", "Successfully deleted " + filename);
    } catch (IOException|RuntimeException e) {
      redirectAttributes.addFlashAttribute("flash.message", "Failed to delete " + filename + " => " + e.getMessage());
    }
    return "redirect:/";
  }
}
