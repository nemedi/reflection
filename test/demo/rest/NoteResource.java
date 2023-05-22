package demo.rest;

import static demo.rest.RestResponse.accepted;
import static demo.rest.RestResponse.created;
import static demo.rest.RestResponse.noContent;
import static demo.rest.RestResponse.notAcceptable;
import static demo.rest.RestResponse.notFound;
import static demo.rest.RestResponse.ok;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestResource("notes")
public class NoteResource {
	
	private static List<Note> notes = new ArrayList<Note>();

	@RestEndpoint(method = "GET")
	public RestResponse getNotes() {
		List<String> result = notes
				.stream()
				.map(note -> note.getTitle())
				.collect(Collectors.toList());
		return result.size() > 0 ?
				ok(result) : noContent();
	}
	
	@RestEndpoint(method = "GET", queries = {"title"})
	public RestResponse getNoteDetails(String title) {
		Optional<Note> result = notes
				.stream()
				.filter(note -> note.getTitle().equalsIgnoreCase(title))
				.findFirst();
		return result.isPresent() ?
				ok(result.get()) : notFound(); 
	}
	
	@RestEndpoint(method = "GET", queries = {"tag"})
	public RestResponse getNotesByTag(String tag) {
		List<String> result = notes
				.stream()
				.filter(note -> note.getTags().contains(tag))
				.map(note -> note.getTitle())
				.collect(Collectors.toList());
		return result.size() > 0 ?
				ok(result) : noContent();
	}
	
	@RestEndpoint(method = "POST", body = "note")
	public RestResponse addNote(Note note) {
		Optional<Note> result = notes
			.stream()
			.filter(existingNote -> existingNote.getTitle().equalsIgnoreCase(note.getTitle()))
			.findAny();
		if (!result.isPresent()) {
			notes.add(note);
			return created(note);
		} else {
			return notAcceptable();
		}
	}
	
	@RestEndpoint(method = "PUT", body = "note")
	public RestResponse editNote(Note note) {
		Optional<Note> result = notes
			.stream()
			.filter(existingNote -> existingNote.getTitle().equalsIgnoreCase(note.getTitle()))
			.findFirst();
		if (result.isPresent()) {
			Note existingNote = result.get();
			existingNote.setContent(note.getContent());
			return accepted();
		} else {
			return notAcceptable();
		}
	}
	
	@RestEndpoint(method = "PUT", queries = {"title"})
	public RestResponse removeNote(String title) {
		Optional<Note> result = notes
			.stream()
			.filter(existingNote -> existingNote.getTitle().equalsIgnoreCase(title))
			.findFirst();
		if (result.isPresent()) {
			notes.remove(result.get());
			return accepted();
		} else {
			return notAcceptable();
		}
	}
	
	@RestEndpoint(method = "POST", queries = {"title", "tag"})
	public RestResponse addTag(String title, String tag) {
		Optional<Note> result = notes
			.stream()
			.filter(existingNote -> existingNote.getTitle().equalsIgnoreCase(title)
					&& !existingNote.getTags().contains(tag))
			.findFirst();
		if (result.isPresent()) {
			result.get().getTags().add(tag);
			return accepted();
		} else {
			return notAcceptable();
		}
	}
	
	@RestEndpoint(method = "DELETE", queries = {"title", "tag"})
	public RestResponse removeTag(String title, String tag) {
		Optional<Note> result = notes
			.stream()
			.filter(existingNote -> existingNote.getTitle().equalsIgnoreCase(title)
					&& existingNote.getTags().contains(tag))
			.findFirst();
		if (result.isPresent()) {
			result.get().getTags().remove(tag);
			return accepted();
		} else {
			return notAcceptable();
		}
	}

}
