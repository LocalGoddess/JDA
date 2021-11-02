package net.dv8tion.jda.internal.requests.restaction.pagination;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.BaseGuildMessageChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildThread;
import net.dv8tion.jda.api.entities.IGuildThreadContainer;
import net.dv8tion.jda.api.exceptions.ParsingException;
import net.dv8tion.jda.api.requests.Request;
import net.dv8tion.jda.api.requests.Response;
import net.dv8tion.jda.api.requests.restaction.pagination.GuildThreadPaginationAction;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.entities.EntityBuilder;
import net.dv8tion.jda.internal.requests.Route;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuildThreadPaginationActionImpl extends PaginationActionImpl<GuildThread, GuildThreadPaginationAction> implements GuildThreadPaginationAction
{
    protected final IGuildThreadContainer channel;

    public GuildThreadPaginationActionImpl(JDA api, Route.CompiledRoute route, BaseGuildMessageChannel channel)
    {
        super(api, route, 1, 100, 100);
        this.channel = channel;
    }

    @Nonnull
    @Override
    public IGuildThreadContainer getChannel()
    {
        return channel;
    }

    @Override
    protected Route.CompiledRoute finalizeRoute()
    {
        Route.CompiledRoute route = super.finalizeRoute();

        final String limit = String.valueOf(this.limit.get());
        final long last = this.lastKey;

        route = route.withQueryParams("limit", limit);

        if (last != 0)
            route = route.withQueryParams("before", Long.toUnsignedString(last));

        return route;
    }

    @Override
    protected void handleSuccess(Response response, Request<List<GuildThread>> request)
    {
        DataObject obj = response.getObject();
        DataArray selfThreadMembers = obj.getArray("members");
        DataArray threads = obj.getArray("threads");

        List<GuildThread> list = new ArrayList<>(threads.length());
        EntityBuilder builder = api.getEntityBuilder();

        TLongObjectMap<DataObject> selfThreadMemberMap = new TLongObjectHashMap<>();
        for (int i = 0; i < selfThreadMembers.length(); i++)
        {
            DataObject selfThreadMember = selfThreadMembers.getObject(i);

            //Store the thread member based on the "id" which is the _thread's_ id, not the member's id (which would be our id)
            selfThreadMemberMap.put(selfThreadMember.getLong("id"), selfThreadMember);
        }

        for (int i = 0; i < threads.length(); i++)
        {
            try
            {
                DataObject threadObj = threads.getObject(i);
                DataObject selfThreadMemberObj = selfThreadMemberMap.get(threadObj.getLong("id", 0));

                if (selfThreadMemberObj != null)
                {
                    //Combine the thread and self thread-member into a single object to model what we get from
                    // thread payloads (like from Gateway, etc)
                    threadObj.put("member", selfThreadMemberObj);
                }

                GuildThread thread = builder.createGuildThread(threadObj, getGuild().getIdLong());
                list.add(thread);

                if (this.useCache)
                    this.cached.add(thread);
                this.last = thread;
                this.lastKey = last.getIdLong();
            }
            catch (ParsingException | NullPointerException e)
            {
                LOG.warn("Encountered exception in GuildThreadPagination", e);
            }
        }

        request.onSuccess(list);
    }

    @Override
    protected long getKey(GuildThread it)
    {
        return it.getIdLong();
    }
}