import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observer Pattern
 *
 * Intent: Define a one-to-many dependency between objects so that when
 *         one object changes state, all its dependents are notified and
 *         updated automatically.
 *
 * Example: A stock market system where:
 *   - StockMarket (Subject) tracks stock prices
 *   - Investors (Observers) want to be notified of price changes
 *   - Different observers react differently (buy, sell, alert)
 *
 * Roles:
 *   Subject          -> StockMarket (interface + implementation)
 *   Observer         -> StockObserver (interface)
 *   ConcreteObserver -> InvestorDisplay, AlertSystem, AutoTrader
 */
public class Observer {

    // =========================================================================
    // Observer Interface
    // =========================================================================

    /**
     * The Observer interface declares the update method.
     * All observers must implement this to receive notifications.
     */
    interface StockObserver {
        /**
         * Called by the Subject when a stock price changes.
         *
         * @param symbol   stock ticker symbol (e.g., "AAPL")
         * @param oldPrice previous price
         * @param newPrice current price
         */
        void update(String symbol, double oldPrice, double newPrice);

        /** Returns the name of this observer for identification. */
        String getName();
    }

    // =========================================================================
    // Subject Interface
    // =========================================================================

    /** The Subject interface — manages observer subscriptions and notifications. */
    interface StockSubject {
        void subscribe(StockObserver observer);
        void unsubscribe(StockObserver observer);
        void notifyObservers(String symbol, double oldPrice, double newPrice);
    }

    // =========================================================================
    // Concrete Subject: StockMarket
    // =========================================================================

    /**
     * The ConcreteSubject. Stores stock prices and notifies observers
     * whenever a price changes.
     */
    static class StockMarket implements StockSubject {
        private final List<StockObserver> observers = new ArrayList<>();
        private final Map<String, Double> prices    = new HashMap<>();

        @Override
        public void subscribe(StockObserver observer) {
            if (!observers.contains(observer)) {
                observers.add(observer);
                System.out.println("[Market] " + observer.getName() + " subscribed.");
            }
        }

        @Override
        public void unsubscribe(StockObserver observer) {
            observers.remove(observer);
            System.out.println("[Market] " + observer.getName() + " unsubscribed.");
        }

        @Override
        public void notifyObservers(String symbol, double oldPrice, double newPrice) {
            // Iterate over a copy to avoid ConcurrentModificationException
            // if an observer modifies the subscription list during notification
            List<StockObserver> snapshot = List.copyOf(observers);
            for (StockObserver observer : snapshot) {
                observer.update(symbol, oldPrice, newPrice);
            }
        }

        /**
         * Updates the price of a stock. If the price changed, all observers
         * are automatically notified.
         */
        public void setPrice(String symbol, double newPrice) {
            double oldPrice = prices.getOrDefault(symbol, 0.0);
            prices.put(symbol, newPrice);

            double change = newPrice - oldPrice;
            double changePct = oldPrice > 0 ? (change / oldPrice) * 100 : 0;
            System.out.printf("%n[Market] %s: $%.2f → $%.2f (%+.2f, %+.1f%%)%n",
                    symbol, oldPrice, newPrice, change, changePct);

            if (oldPrice != newPrice) {
                notifyObservers(symbol, oldPrice, newPrice);
            }
        }

        public double getPrice(String symbol) {
            return prices.getOrDefault(symbol, 0.0);
        }

        public Map<String, Double> getAllPrices() { return Map.copyOf(prices); }
    }

    // =========================================================================
    // Concrete Observers
    // =========================================================================

    /**
     * A display board that shows price changes.
     * Stores a history of price alerts it has received.
     */
    static class InvestorDisplay implements StockObserver {
        private final String name;
        private final List<String> priceHistory = new ArrayList<>();

        InvestorDisplay(String name) { this.name = name; }

        @Override
        public void update(String symbol, double oldPrice, double newPrice) {
            double change = newPrice - oldPrice;
            String direction = change > 0 ? "▲" : "▼";
            String entry = String.format("%s %s $%.2f (was $%.2f)", symbol, direction, newPrice, oldPrice);
            priceHistory.add(entry);
            System.out.printf("  [%s] %s%n", name, entry);
        }

        @Override
        public String getName() { return name; }

        public List<String> getHistory() { return List.copyOf(priceHistory); }
    }

    /**
     * An alert system that triggers alerts when price drops exceed a threshold.
     */
    static class AlertSystem implements StockObserver {
        private final double dropThresholdPct;   // trigger alert if price drops by this %
        private int alertCount = 0;

        AlertSystem(double dropThresholdPct) {
            this.dropThresholdPct = dropThresholdPct;
        }

        @Override
        public void update(String symbol, double oldPrice, double newPrice) {
            if (oldPrice <= 0) return;
            double changePct = ((newPrice - oldPrice) / oldPrice) * 100;

            if (changePct <= -dropThresholdPct) {
                alertCount++;
                System.out.printf("  🚨 [AlertSystem] ALERT #%d: %s dropped %.1f%% (%.2f → %.2f)!%n",
                        alertCount, symbol, Math.abs(changePct), oldPrice, newPrice);
            }
        }

        @Override
        public String getName() { return "AlertSystem(-" + dropThresholdPct + "%)"; }

        public int getAlertCount() { return alertCount; }
    }

    /**
     * An automatic trading bot that buys when price drops significantly
     * and sells when price rises significantly.
     */
    static class AutoTrader implements StockObserver {
        private final String name;
        private final double buyThresholdPct;    // buy if drops by this %
        private final double sellThresholdPct;   // sell if rises by this %
        private double portfolioValue = 10000.0; // starting value
        private final List<String> trades = new ArrayList<>();

        AutoTrader(String name, double buyThresholdPct, double sellThresholdPct) {
            this.name             = name;
            this.buyThresholdPct  = buyThresholdPct;
            this.sellThresholdPct = sellThresholdPct;
        }

        @Override
        public void update(String symbol, double oldPrice, double newPrice) {
            if (oldPrice <= 0) return;
            double changePct = ((newPrice - oldPrice) / oldPrice) * 100;

            if (changePct <= -buyThresholdPct) {
                // Price dropped — BUY signal
                int shares = (int)(portfolioValue * 0.1 / newPrice);  // invest 10%
                double cost = shares * newPrice;
                portfolioValue -= cost;
                String trade = String.format("BUY  %d %s @ $%.2f (cost $%.2f)", shares, symbol, newPrice, cost);
                trades.add(trade);
                System.out.printf("  📈 [%s] %s%n", name, trade);
            } else if (changePct >= sellThresholdPct) {
                // Price rose — SELL signal
                String trade = String.format("SELL %s @ $%.2f (+%.1f%%)", symbol, newPrice, changePct);
                trades.add(trade);
                portfolioValue += newPrice * 5;  // simplified
                System.out.printf("  📉 [%s] %s%n", name, trade);
            }
        }

        @Override
        public String getName() { return name; }

        public List<String> getTrades() { return List.copyOf(trades); }
        public double getPortfolioValue() { return portfolioValue; }
    }

    // =========================================================================
    // Demo
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Observer Pattern Demo — Stock Market ===");

        StockMarket market = new StockMarket();

        // Create observers
        InvestorDisplay display1  = new InvestorDisplay("Alice's Dashboard");
        InvestorDisplay display2  = new InvestorDisplay("Bob's Terminal");
        AlertSystem     alerts    = new AlertSystem(5.0);    // alert on 5% drop
        AutoTrader      bot       = new AutoTrader("TradingBot", 3.0, 5.0);

        // Subscribe observers
        System.out.println("\n--- Subscribing observers ---");
        market.subscribe(display1);
        market.subscribe(display2);
        market.subscribe(alerts);
        market.subscribe(bot);

        // Simulate market activity
        System.out.println("\n--- Market activity ---");
        market.setPrice("AAPL", 150.00);
        market.setPrice("AAPL", 148.50);    // small drop
        market.setPrice("AAPL", 139.50);    // 7% drop — should trigger alert + buy
        market.setPrice("GOOG", 2800.00);
        market.setPrice("GOOG", 2954.00);   // 5.5% rise — should trigger sell

        // Unsubscribe Bob
        System.out.println("\n--- Bob unsubscribes ---");
        market.unsubscribe(display2);

        market.setPrice("AAPL", 155.00);    // recovery — Bob won't see this

        // Summary
        System.out.println("\n--- Summary ---");
        System.out.println("Alice's price history:");
        display1.getHistory().forEach(h -> System.out.println("  " + h));

        System.out.println("\nBob's price history (no AAPL recovery):");
        display2.getHistory().forEach(h -> System.out.println("  " + h));

        System.out.println("\nAlert system fired " + alerts.getAlertCount() + " alerts.");

        System.out.println("\nBot trades:");
        bot.getTrades().forEach(t -> System.out.println("  " + t));
        System.out.printf("Bot portfolio value: $%.2f%n", bot.getPortfolioValue());
    }
}
