package ma.emsi.houdaifa.webtp2houdaifa.llm;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.enterprise.context.Dependent;

import java.io.Serializable;

@Dependent
public class LlmClient implements Serializable {

    private String systemRole;
    private Assistant assistant;
    private ChatMemory chatMemory;

    public LlmClient() {
        // Initialisation du modèle LLM avec la clé API et les paramètres
        String llmKey = System.getenv("GEMINI_KEY");
        ChatLanguageModel model = GoogleAiGeminiChatModel.builder()
                .apiKey(llmKey)
                .modelName("gemini-1.5-flash")
                .temperature(0.7)
                .build();

        // Initialisation de la mémoire de chat avec une fenêtre de messages
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // Création du service d'assistant
        this.assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(this.chatMemory)
                .build();
    }

    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
        // Réinitialiser la mémoire et ajouter un message système avec le rôle
        this.chatMemory.clear();
        this.chatMemory.add(new SystemMessage(this.systemRole));
    }

    public String sendMessageToLlm(String question) {
        // Envoyer la question à l'assistant et retourner la réponse
        return this.assistant.chat(question);
    }

    // Interface représentant l'assistant qui interagit avec le modèle LLM
    public interface Assistant {
        String chat(String prompt);
    }
}
