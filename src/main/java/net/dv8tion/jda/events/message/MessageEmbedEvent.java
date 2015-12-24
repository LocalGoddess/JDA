/**
 *    Copyright 2015 Austin Keener & Michael Ritter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.dv8tion.jda.events.message;

import java.util.List;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.entities.MessageEmbed;
import net.dv8tion.jda.entities.TextChannel;

public class MessageEmbedEvent extends GenericMessageEvent
{
    protected String messageId;
    protected TextChannel channel;
    protected List<MessageEmbed> embeds;

    public MessageEmbedEvent(JDA api, int responseNumber, String messageId, TextChannel channel, List<MessageEmbed> embeds)
    {
        super(api, responseNumber, null);
        this.messageId = messageId;
        this.channel = channel;
        this.embeds = embeds;
    }

    public String getMessageId()
    {
        return messageId;
    }

    public TextChannel getChannel()
    {
        return channel;
    }

    public List<MessageEmbed> getMessageEmbeds()
    {
        return embeds;
    }
}