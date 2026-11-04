package com.alimapps.senbombardir.ui.model

import com.alimapps.senbombardir.ui.model.types.ChampionTitle

data class ChampionPlayerUiModel(
    val playerUiModel: PlayerUiModel,
    val title: ChampionTitle,
)

data class ChampionsUiModel(
    val teamUiModel: TeamUiModel,
    val players: List<ChampionPlayerUiModel>,
) {

    companion object {

        fun make(team: TeamUiModel, players: List<PlayerUiModel>): ChampionsUiModel =
            ChampionsUiModel(teamUiModel = team, players = assignTitles(players))

        private data class Candidate(
            val playerIndex: Int,
            val title: ChampionTitle,
            val strength: Double,
        )

        private fun score(player: PlayerUiModel): Int =
            (player.goals * 3) + (player.assists * 2) + (player.saves * 2) + player.tackles + player.dribbles + player.passes + player.shots - player.yellowCards - (player.redCards * 3)

        private fun contributionCount(player: PlayerUiModel): Int =
            listOf(player.goals, player.assists, player.saves, player.tackles, player.dribbles, player.passes, player.shots)
                .count { it > 0 }

        private fun actionsCount(player: PlayerUiModel): Int =
            player.goals + player.assists + player.saves + player.tackles + player.dribbles + player.passes + player.shots

        private fun isEmpty(player: PlayerUiModel): Boolean =
            contributionCount(player) == 0 && player.yellowCards == 0 && player.redCards == 0

        private fun assignTitles(players: List<PlayerUiModel>): List<ChampionPlayerUiModel> {
            if (players.isEmpty()) return emptyList()

            val candidates = mutableListOf<Candidate>()

            val scores = players.map(::score)
            val bestScore = scores.max()
            if (bestScore > 0) {
                candidates.add(Candidate(playerIndex = scores.indexOf(bestScore), title = ChampionTitle.MatchKing, strength = 10.0))
            }

            val statTitles = listOf<Triple<ChampionTitle, (PlayerUiModel) -> Int, Double>>(
                Triple(ChampionTitle.Sniper, { it.goals }, 1.0),
                Triple(ChampionTitle.Wall, { it.saves }, 0.98),
                Triple(ChampionTitle.Playmaker, { it.assists }, 0.96),
                Triple(ChampionTitle.Pitbull, { it.tackles }, 0.9),
                Triple(ChampionTitle.Magician, { it.dribbles }, 0.88),
                Triple(ChampionTitle.Maestro, { it.passes }, 0.86),
                Triple(ChampionTitle.Cannon, { it.shots }, 0.84),
            )

            statTitles.forEach { (title, selector, weight) ->
                val teamMax = players.maxOf(selector)
                if (teamMax <= 0) return@forEach
                players.forEachIndexed { index, player ->
                    val value = selector(player)
                    if (value > 0) {
                        candidates.add(Candidate(playerIndex = index, title = title, strength = value.toDouble() / teamMax * weight))
                    }
                }
            }

            players.forEachIndexed { index, player ->
                val contributions = contributionCount(player)
                if (contributions >= 4) {
                    candidates.add(Candidate(playerIndex = index, title = ChampionTitle.AllRounder, strength = 0.8 + contributions * 0.03))
                }
                if (player.shots >= 3 && player.goals == 0) {
                    candidates.add(Candidate(playerIndex = index, title = ChampionTitle.OffTarget, strength = 0.95 + player.shots * 0.01))
                }
                val cardWeight = player.yellowCards + player.redCards * 3
                if (cardWeight > 0) {
                    val base = if (player.redCards > 0) 1.05 else 0.7
                    candidates.add(Candidate(playerIndex = index, title = ChampionTitle.Hothead, strength = base + cardWeight * 0.01))
                }
            }

            val titles = mutableMapOf<Int, ChampionTitle>()
            val usedTitles = mutableSetOf<ChampionTitle>()
            candidates.sortedByDescending { it.strength }.forEach { candidate ->
                if (titles[candidate.playerIndex] == null && candidate.title !in usedTitles) {
                    titles[candidate.playerIndex] = candidate.title
                    usedTitles.add(candidate.title)
                }
            }

            val leftovers = players.indices.filter { titles[it] == null }

            leftovers
                .filter { !isEmpty(players[it]) }
                .sortedByDescending { actionsCount(players[it]) }
                .forEachIndexed { position, index ->
                    titles[index] = ChampionTitle.activeFallbacks[position % ChampionTitle.activeFallbacks.size]
                }

            leftovers
                .filter { isEmpty(players[it]) }
                .forEachIndexed { position, index ->
                    titles[index] = ChampionTitle.emptyFallbacks[position % ChampionTitle.emptyFallbacks.size]
                }

            return players
                .mapIndexed { index, player ->
                    ChampionPlayerUiModel(playerUiModel = player, title = titles[index] ?: ChampionTitle.TeamPlayer)
                }
                .sortedBy { it.title.ordinal }
        }
    }
}
