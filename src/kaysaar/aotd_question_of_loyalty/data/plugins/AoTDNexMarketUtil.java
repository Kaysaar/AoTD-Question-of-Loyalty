package kaysaar.aotd_question_of_loyalty.data.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CommDirectoryEntryAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;

import java.util.HashSet;
import java.util.Set;

import static exerelin.campaign.ColonyManager.NEEDED_OFFICIALS;

public class AoTDNexMarketUtil {
    //Code taken from Nexerlin from Histdline
    public static boolean hasPerson(MarketAPI market, String postId)
    {
        return getPerson(market, postId) != null;
    }
    public static boolean doesPlayerHaveMarkets(boolean includeNonPlayerFactionMarkets){
        return Misc.getPlayerMarkets(includeNonPlayerFactionMarkets).stream().anyMatch(x -> !x.hasTag("nex_playerOutpost"));
    }
    public static PersonAPI getPerson(MarketAPI market, String postId)
    {
        for (CommDirectoryEntryAPI dir : market.getCommDirectory().getEntriesCopy())
        {
            if (dir.getType() != CommDirectoryEntryAPI.EntryType.PERSON) continue;
            PersonAPI person = (PersonAPI)dir.getEntryData();
            if (person.getPostId().equals(postId))
                return person;
        }
        return null;
    }

    public static PersonAPI addPerson(ImportantPeopleAPI ip, MarketAPI market,
                                      String rankId, String postId, boolean noDuplicate)
    {
        if (noDuplicate && hasPerson(market, postId))
            return null;

        PersonAPI person = market.getFaction().createRandomPerson();
        if (rankId != null) person.setRankId(rankId);
        person.setPostId(postId);

        market.getCommDirectory().addPerson(person);
        market.addPerson(person);
        ip.addPerson(person);
        ip.getData(person).getLocation().setMarket(market);
        ip.checkOutPerson(person, "permanent_staff");

        if (postId.equals(Ranks.POST_BASE_COMMANDER) || postId.equals(Ranks.POST_STATION_COMMANDER)
                || postId.equals(Ranks.POST_ADMINISTRATOR))
        {
            if (market.getSize() >= 8) {
                person.setImportanceAndVoice(PersonImportance.VERY_HIGH, StarSystemGenerator.random);
            } else if (market.getSize() >= 6) {
                person.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
            } else {
                person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
            }
        } else if (postId.equals(Ranks.POST_PORTMASTER)) {
            if (market.getSize() >= 8) {
                person.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);
            } else if (market.getSize() >= 6) {
                person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
            } else if (market.getSize() >= 4) {
                person.setImportanceAndVoice(PersonImportance.LOW, StarSystemGenerator.random);
            } else {
                person.setImportanceAndVoice(PersonImportance.VERY_LOW, StarSystemGenerator.random);
            }
        } else if (postId.equals(Ranks.POST_SUPPLY_OFFICER)) {
            if (market.getSize() >= 6) {
                person.setImportanceAndVoice(PersonImportance.MEDIUM, StarSystemGenerator.random);
            } else if (market.getSize() >= 4) {
                person.setImportanceAndVoice(PersonImportance.LOW, StarSystemGenerator.random);
            } else {
                person.setImportanceAndVoice(PersonImportance.VERY_LOW, StarSystemGenerator.random);
            }
        }

        return person;
    }
    public static  void  addOrUpdateOfficials(MarketAPI market) {
        Set<String> officialsPresent = new HashSet<>();

        for (CommDirectoryEntryAPI dir : market.getCommDirectory().getEntriesCopy())
        {
            if (dir.getType() != CommDirectoryEntryAPI.EntryType.PERSON) continue;
            PersonAPI person = (PersonAPI)dir.getEntryData();
            if (person.getFaction() != market.getFaction()) continue;
            if (!NEEDED_OFFICIALS.contains(person.getPostId())) continue;
            officialsPresent.add(person.getPostId());
        }

        // Base commander
        boolean havePerson = !officialsPresent.isEmpty();
        if (market.hasIndustry(Industries.MILITARYBASE) || market.hasIndustry(Industries.HIGHCOMMAND)) {
            String rankId = Ranks.GROUND_MAJOR;
            if (market.getSize() >= 6) {
                rankId = Ranks.GROUND_GENERAL;
            } else if (market.getSize() >= 4) {
                rankId = Ranks.GROUND_COLONEL;
            }
            addOrUpdateOfficial(market, rankId, Ranks.POST_BASE_COMMANDER, officialsPresent);
            havePerson = true;
        }

        // Station commander
        boolean hasStation = false;
        for (Industry curr : market.getIndustries()) {
            if (curr.getSpec().hasTag(Industries.TAG_STATION)) {
                hasStation = true;
                break;
            }
        }
        if (hasStation) {
            String rankId = Ranks.SPACE_COMMANDER;
            if (market.getSize() >= 6) {
                rankId = Ranks.SPACE_ADMIRAL;
            } else if (market.getSize() >= 4) {
                rankId = Ranks.SPACE_CAPTAIN;
            }
            addOrUpdateOfficial(market, rankId, Ranks.POST_STATION_COMMANDER, officialsPresent);
            havePerson = true;
        }

        // Portmaster
        if (market.hasSpaceport()) {
            addOrUpdateOfficial(market, null, Ranks.POST_PORTMASTER, officialsPresent);
            havePerson = true;
        }

        // Supply officer
        if (havePerson) {
            addOrUpdateOfficial(market, Ranks.SPACE_COMMANDER, Ranks.POST_SUPPLY_OFFICER, officialsPresent);
            havePerson = true;
        }

        // Administrator
        if (true || !havePerson) {
            addOrUpdateOfficial(market, Ranks.CITIZEN, Ranks.POST_ADMINISTRATOR, officialsPresent);
            havePerson = true;
        }
    }
    public static void addOrUpdateOfficial(MarketAPI market, String rankId, String postId,
                                    Set<String> postsPresent)
    {
        if (postsPresent.contains(postId) && rankId != null) {
            PersonAPI person = AoTDNexMarketUtil.getPerson(market, postId);
            if (person != null && !rankId.equals(person.getRankId()))
                person.setRankId(rankId);
            return;
        }
        AoTDNexMarketUtil.addPerson(Global.getSector().getImportantPeople(),
                market, rankId, postId, true);
        postsPresent.add(postId);
    }
}
