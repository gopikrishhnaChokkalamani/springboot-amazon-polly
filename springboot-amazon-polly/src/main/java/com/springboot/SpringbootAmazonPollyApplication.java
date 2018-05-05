package com.springboot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.VoiceId;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

@SpringBootApplication
public class SpringbootAmazonPollyApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAmazonPollyApplication.class, args);
	}

	@Autowired
	AmazonPolly amazonPolly;

	//@Autowired
	//Voice getVoice;

	@Autowired
	SynthesizeSpeechRequest synthesizeSpeechRequest;

	@Override
	public void run(String... arg0) throws Exception {
		// TODO Auto-generated method stub
		readFileForSynthesize();
	}

	public void synthesizeFromStringAndPlay(String text) throws IOException, JavaLayerException {
		SynthesizeSpeechRequest synthReq = synthesizeSpeechRequest.withText(text).withVoiceId(VoiceId.Raveena)
				.withOutputFormat(OutputFormat.Mp3);
		SynthesizeSpeechResult synthRes = amazonPolly.synthesizeSpeech(synthReq);
		InputStream speechStream = synthRes.getAudioStream();
		AdvancedPlayer player = new AdvancedPlayer(speechStream,
				javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());

		player.setPlayBackListener(new PlaybackListener() {
			@Override
			public void playbackStarted(PlaybackEvent evt) {
				System.out.println("Playback started");
			}

			@Override
			public void playbackFinished(PlaybackEvent evt) {
				System.out.println("Playback finished");
			}
		});

		// Play the Text
		player.play();
	}

	public void synthesizeAndSaveToFile(String text, String outputFileName) {
		SynthesizeSpeechRequest request = synthesizeSpeechRequest.withOutputFormat(OutputFormat.Mp3)
				.withVoiceId(VoiceId.Raveena).withText(text);
		try (FileOutputStream outputStream = new FileOutputStream(new File(outputFileName))) {
			SynthesizeSpeechResult synthesizeSpeechResult = amazonPolly.synthesizeSpeech(request);
			byte[] buffer = new byte[2 * 1024];
			int readBytes;

			try (InputStream in = synthesizeSpeechResult.getAudioStream()) {
				while ((readBytes = in.read(buffer)) > 0) {
					outputStream.write(buffer, 0, readBytes);
				}
			}
		} catch (Exception e) {
			System.err.println("Exception caught: " + e);
		}
	}

	public void readFileForSynthesize() throws JavaLayerException {
		String line = "";
		StringBuilder builder = new StringBuilder();
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("/Users/yourusername/Documents/textData.txt"))))) {
			while ((line = bufferedReader.readLine()) != null) {
				builder.append(line);
			}
			System.out.println(builder.toString());

			// Play the Text
			synthesizeFromStringAndPlay(builder.toString());

			// Save to local drive
			synthesizeAndSaveToFile(builder.toString(), "/Users/yourusername/Documents/amazon-speech.mp3");
		} catch (IOException ex) {
			System.err.println("Exception caught: " + ex);
		}
	}
}
